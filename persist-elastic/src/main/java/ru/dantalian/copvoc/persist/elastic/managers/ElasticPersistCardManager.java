package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.query.BoolExpression;
import ru.dantalian.copvoc.persist.api.query.CardsExpression;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;
import ru.dantalian.copvoc.persist.api.query.TermExpression;
import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;
import ru.dantalian.copvoc.persist.elastic.model.DbCard;
import ru.dantalian.copvoc.persist.impl.model.PojoCard;
import ru.dantalian.copvoc.persist.impl.model.PojoCardFieldContent;

@Service
public class ElasticPersistCardManager extends AbstractPersistManager<DbCard>
	implements PersistCardManager {

	private static final String DEFAULT_INDEX = "cards";

	private final ElasticSettings settings;

	@Autowired
	public ElasticPersistCardManager(final RestHighLevelClient aClient, final ElasticSettings aSettings) {
		super(aClient, DbCard.class);
		settings = aSettings;
	}

	@Override
	protected String getDefaultIndex() {
		return DEFAULT_INDEX;
	}

	@Override
	protected XContentBuilder getSettings(final String aIndex) throws PersistException {
		return settings.getDefaultSettings();
	}

	@Override
	public Card createCard(final String aUser, final UUID aVocabularyId,
			final Map<String, String> aContent) throws PersistException {
		final UUID id = UUID.randomUUID();
		final DbCard card = new DbCard(id, aVocabularyId, aContent);
		add(getIndexId(aVocabularyId), card, true);
		return asCard(card);
	}

	@Override
	public void updateCard(final String aUser, final UUID aVocabularyId, final UUID aId, final Map<String, String> aContent)
			throws PersistException {
		final Card card = getCard(aUser, aVocabularyId, aId);
		if (card == null) {
			throw new PersistException("Card not found");
		}
		final DbCard dbCard = asDbCard(card);
		update(getIndexId(card.getVocabularyId()), dbCard, true);
	}

	@Override
	public Card getCard(final String aUser, final UUID aVocabularyId, final UUID aId) throws PersistException {
		return asCard(get(getIndexId(aVocabularyId), aId.toString()));
	}

	@Override
	public void deleteCard(final String aUser, final UUID aVocabularyId, final UUID aId) throws PersistException {
		delete(getIndexId(aVocabularyId), aId.toString());
	}

	@Override
	public List<Card> queryCards(final String aUser, final CardsQuery aQuery) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		final QueryBuilder query = asElaticQuery(aQuery);
		searchSourceBuilder.query(query);
		final SearchResponse search = search(getIndexId(aQuery.getVocabularyId()), searchSourceBuilder);
		final List<Card> list = new LinkedList<>();
		search.getHits()
				.forEach(aItem -> {
					final Map<String, Object> source = aItem.getSourceAsMap();
					final UUID id = UUID.fromString(aItem.getId());
					final UUID vocId = UUID.fromString((String) source.get("vocabulary_id"));

					final Map<String, ?> content = (Map<String, ?>) source.get("content");
					final Map<String, CardFieldContent> map = new HashMap<>();
					for (final Entry<String, ?> entry: content.entrySet()) {
						map.put(entry.getKey(), new PojoCardFieldContent(id, vocId, entry.getKey(), (String) entry.getValue()));
					}
					list.add(new PojoCard(id, vocId, map));
					});
		return list;
	}

	private QueryBuilder asElaticQuery(final CardsQuery aQuery) {
		final BoolQueryBuilder bool = QueryBuilders.boolQuery();
		if (aQuery.getVocabularyId() != null) {
			bool.must(QueryBuilders.termQuery("vocabulary_id", aQuery.getVocabularyId().toString()));
		}
		bool.must(asElaticQuery(aQuery.where()));
		return bool.must().isEmpty() ? QueryBuilders.matchAllQuery() : bool;
	}

	private QueryBuilder asElaticQuery(final CardsExpression aExpression) {
		if (aExpression instanceof TermExpression) {
			final TermExpression termExpression = (TermExpression) aExpression;
			if (termExpression.isWildcard()) {
				return QueryBuilders.wildcardQuery(termExpression.getName(), termExpression.getValue());
			} else {
				return QueryBuilders.termQuery(termExpression.getName(), termExpression.getValue());
			}
		} else if (aExpression instanceof BoolExpression) {
			final BoolQueryBuilder boolElastic = QueryBuilders.boolQuery();
			final BoolExpression boolExpression = (BoolExpression) aExpression;
			for (final CardsExpression exp: boolExpression.must()) {
				boolElastic.must(asElaticQuery(exp));
			}
			for (final CardsExpression exp: boolExpression.should()) {
				boolElastic.should(asElaticQuery(exp));
			}
			for (final CardsExpression exp: boolExpression.not()) {
				boolElastic.mustNot(asElaticQuery(exp));
			}
			return boolElastic;
		} else {
			throw new IllegalArgumentException("Unknown expression type: " + aExpression.getClass().getName());
		}
	}

	private String getIndexId(final UUID aUuid) {
		if (aUuid == null) {
			return DEFAULT_INDEX + "-*";
		}
		return DEFAULT_INDEX + "-" + aUuid.toString();
	}

	private DbCard asDbCard(final Card aCard) {
		final Map<String, CardFieldContent> fieldsContent = aCard.getFieldsContent();
		final Map<String, String> content = new HashMap<>();
		for (final Entry<String, CardFieldContent> entry: fieldsContent.entrySet()) {
			content.put(entry.getKey(), entry.getValue().getContent());
		}
		return new DbCard(aCard.getId(), aCard.getVocabularyId(), content);
	}

	private Card asCard(final DbCard aDbCard) {
		final Map<String, String> fieldsContent = aDbCard.getFieldsContent();
		final Map<String, CardFieldContent> map = new HashMap<>();
		for (final Entry<String, String> entry: fieldsContent.entrySet()) {
			map.put(entry.getKey(), new PojoCardFieldContent(aDbCard.getId(), aDbCard.getVocabularyId(),
					entry.getKey(), entry.getValue()));
		}
		return new PojoCard(aDbCard.getId(), aDbCard.getVocabularyId(), map);
	}

}
