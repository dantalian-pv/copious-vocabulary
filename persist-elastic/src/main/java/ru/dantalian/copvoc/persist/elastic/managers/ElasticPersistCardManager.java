package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.HashMap;
import java.util.Iterator;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.query.BoolExpression;
import ru.dantalian.copvoc.persist.api.query.CardsExpression;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;
import ru.dantalian.copvoc.persist.api.query.TermExpression;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;
import ru.dantalian.copvoc.persist.elastic.model.DbCard;
import ru.dantalian.copvoc.persist.elastic.utils.CardUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoCard;
import ru.dantalian.copvoc.persist.impl.model.PojoCardFieldContent;

@Service
public class ElasticPersistCardManager extends AbstractPersistManager<DbCard>
	implements PersistCardManager {

	private static final String DEFAULT_INDEX = "cards";

	private final ElasticSettings settings;

	private final PersistCardFieldManager fieldManager;

	private final PersistVocabularyManager vocManager;

	@Autowired
	public ElasticPersistCardManager(final RestHighLevelClient aClient, final ElasticSettings aSettings,
			final PersistCardFieldManager aFieldManager, final PersistVocabularyManager aVocManager) {
		super(aClient, DbCard.class);
		settings = aSettings;
		fieldManager = aFieldManager;
		vocManager = aVocManager;
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
		final List<CardField> fields = fieldManager.listFields(aUser, aVocabularyId);
		final Vocabulary vocabulary = vocManager.getVocabulary(aUser, aVocabularyId);
		final DbCard card = new DbCard(id, aVocabularyId,
			LanguageUtils.asString(vocabulary.getSource()),
			LanguageUtils.asString(vocabulary.getTarget()),
			asPersistMap(aContent, fields));
		add(getIndexId(aVocabularyId), card, true);
		return asCard(card, vocabulary);
	}

	@Override
	public Card updateCard(final String aUser, final UUID aVocabularyId, final UUID aId, final Map<String, String> aContent)
			throws PersistException {
		final Card card = getCard(aUser, aVocabularyId, aId);
		if (card == null) {
			throw new PersistException("Card not found");
		}
		final List<CardField> fields = fieldManager.listFields(aUser, aVocabularyId);
		final Vocabulary vocabulary = vocManager.getVocabulary(aUser, aVocabularyId);
		final DbCard dbCard = new DbCard(aId, aVocabularyId,
				LanguageUtils.asString(vocabulary.getSource()),
				LanguageUtils.asString(vocabulary.getTarget()),
				asPersistMap(aContent, fields));
		update(getIndexId(card.getVocabularyId()), dbCard, true);
		return asCard(dbCard, vocabulary);
	}

	@Override
	public Card getCard(final String aUser, final UUID aVocabularyId, final UUID aId) throws PersistException {
		final Vocabulary vocabulary = vocManager.getVocabulary(aUser, aVocabularyId);
		return asCard(get(getIndexId(aVocabularyId), aId.toString()), vocabulary);
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
		final Iterator<SearchHit> iterator = search.getHits().iterator();
		while(iterator.hasNext()) {
			final SearchHit hit = iterator.next();
			final Map<String, Object> source = hit.getSourceAsMap();
			final UUID id = UUID.fromString(hit.getId());
			final UUID vocId = UUID.fromString((String) source.get("vocabulary_id"));
			final Vocabulary vocabulary = vocManager.getVocabulary(aUser, vocId);

			final Map<String, ?> content = (Map<String, ?>) source.get("content");
			final Map<String, CardFieldContent> map = new HashMap<>();
			for (final Entry<String, ?> entry: content.entrySet()) {
				final String pojoName = CardUtils.asPojoName(entry.getKey());
				map.put(pojoName, new PojoCardFieldContent(id, vocId, pojoName, (String) entry.getValue()));
			}
			list.add(new PojoCard(id, vocId,
					LanguageUtils.asString(vocabulary.getSource()),
					LanguageUtils.asString(vocabulary.getTarget()),
					map));
		}
		return list;
	}

	private Map<String, String> asPersistMap(final Map<String, String> aContent, final List<CardField> aFields) {
		final Map<String, CardField> fieldMap = CardUtils.asFieldsMap(aFields);
		final Map<String, String> map = new HashMap<>();
		for (final Entry<String, String> entry: aContent.entrySet()) {
			map.put(CardUtils.asPersistName(fieldMap.get(entry.getKey())), entry.getValue());
		}
		return map;
	}

	private QueryBuilder asElaticQuery(final CardsQuery aQuery) {
		final BoolQueryBuilder bool = QueryBuilders.boolQuery();
		if (aQuery.getVocabularyId() != null) {
			bool.must(QueryBuilders.termQuery("vocabulary_id", aQuery.getVocabularyId().toString()));
		}
		if (aQuery.where() != null) {
			bool.must(asElaticQuery(aQuery.where()));
		}
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

	private DbCard asDbCard(final Card aCard, final List<CardField> aFields, final Vocabulary aVocabulary) {
		final Map<String, CardFieldContent> fieldsContent = aCard.getFieldsContent();
		final Map<String, CardField> fieldMap = CardUtils.asFieldsMap(aFields);
		final Map<String, String> content = new HashMap<>();
		for (final Entry<String, CardFieldContent> entry: fieldsContent.entrySet()) {
			final String persistName = CardUtils.asPersistName(fieldMap.get(entry.getKey()));
			content.put(persistName, entry.getValue().getContent());
		}
		return new DbCard(aCard.getId(), aCard.getVocabularyId(),
			LanguageUtils.asString(aVocabulary.getSource()),
			LanguageUtils.asString(aVocabulary.getTarget()),
			content);
	}

	private Card asCard(final DbCard aDbCard, final Vocabulary aVocabulary) {
		final Map<String, String> fieldsContent = aDbCard.getFieldsContent();
		final Map<String, CardFieldContent> map = new HashMap<>();
		for (final Entry<String, String> entry: fieldsContent.entrySet()) {
			final String name = CardUtils.asPojoName(entry.getKey());
			map.put(name, new PojoCardFieldContent(aDbCard.getId(), aDbCard.getVocabularyId(),
					name, entry.getValue()));
		}
		return new PojoCard(aDbCard.getId(), aDbCard.getVocabularyId(),
				LanguageUtils.asString(aVocabulary.getSource()),
				LanguageUtils.asString(aVocabulary.getTarget()),
				map);
	}

}
