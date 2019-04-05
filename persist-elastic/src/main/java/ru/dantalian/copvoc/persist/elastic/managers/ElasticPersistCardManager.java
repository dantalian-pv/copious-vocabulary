package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
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
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.elastic.model.DbCard;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORM;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORMFactory;
import ru.dantalian.copvoc.persist.elastic.utils.CardUtils;
import ru.dantalian.copvoc.persist.elastic.utils.ElasticQueryUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoCard;
import ru.dantalian.copvoc.persist.impl.model.PojoCardFieldContent;

@Service
public class ElasticPersistCardManager implements PersistCardManager {

	private static final String DEFAULT_INDEX = "cards";

	@Autowired
	private DefaultSettingsProvider settingsProvider;

	@Autowired
	private ElasticORMFactory ormFactory;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private PersistVocabularyManager vocManager;

	private ElasticORM<DbCard> orm;

	@PostConstruct
	public void init() {
		orm = ormFactory.newElasticORM(DbCard.class, settingsProvider);
	}

	@Override
	public Card createCard(final String aUser, final UUID aVocabularyId,
			final Map<String, String> aContent, final Map<String, CardStat> aStatsMap) throws PersistException {
		final UUID id = UUID.randomUUID();
		final List<CardField> fields = fieldManager.listFields(aUser, aVocabularyId);
		final Vocabulary vocabulary = vocManager.getVocabulary(aUser, aVocabularyId);

		final DbCard card = new DbCard(id, aVocabularyId,
			LanguageUtils.asString(vocabulary.getSource()),
			LanguageUtils.asString(vocabulary.getTarget()),
			asPersistMap(aContent, fields),
			CardUtils.asPersistStats(aStatsMap));
		orm.add(getIndexId(aVocabularyId), card, true);
		return asCard(card, vocabulary);
	}

	@Override
	public Card updateCard(final String aUser, final UUID aVocabularyId, final UUID aId,
			final Map<String, String> aContent) throws PersistException {
		final DbCard card = getDbCard(aUser, aVocabularyId, aId);
		if (card == null) {
			throw new PersistException("Card not found");
		}
		final List<CardField> fields = fieldManager.listFields(aUser, aVocabularyId);
		final Vocabulary vocabulary = vocManager.getVocabulary(aUser, aVocabularyId);
		final DbCard dbCard = new DbCard(aId, aVocabularyId,
				LanguageUtils.asString(vocabulary.getSource()),
				LanguageUtils.asString(vocabulary.getTarget()),
				asPersistMap(aContent, fields),
				card.getStats());
		orm.update(getIndexId(card.getVocabularyId()), dbCard, true);
		return asCard(dbCard, vocabulary);
	}

	@Override
	public Card updateStats(final String aUser, final UUID aVocabularyId, final UUID aId, final Map<String, CardStat> aStatsMap) throws PersistException {
		final DbCard card = getDbCard(aUser, aVocabularyId, aId);
		if (card == null) {
			throw new PersistException("Card not found");
		}
		final Map<String, Object> stats = new HashMap<>(card.getStats());
		stats.putAll(CardUtils.asPersistStats(aStatsMap));
		final Vocabulary vocabulary = vocManager.getVocabulary(aUser, aVocabularyId);
		final DbCard dbCard = new DbCard(aId, aVocabularyId,
				LanguageUtils.asString(vocabulary.getSource()),
				LanguageUtils.asString(vocabulary.getTarget()),
				card.getFieldsContent(),
				stats);
		orm.update(getIndexId(card.getVocabularyId()), dbCard, true);
		return asCard(dbCard, vocabulary);
	}

	@Override
	public void updateStatForCard(final String aUser, final UUID aVocabularyId, final UUID aCardId,
			final CardStatAction aAction)
			throws PersistException {
		orm.updateByScript(getIndexId(aVocabularyId), aCardId.toString(),
				ElasticQueryUtils.asElasticScript(aAction), false);
	}

	@Override
	public Card getCard(final String aUser, final UUID aVocabularyId, final UUID aId) throws PersistException {
		final Vocabulary vocabulary = vocManager.getVocabulary(aUser, aVocabularyId);
		return asCard(getDbCard(aUser, aVocabularyId, aId), vocabulary);
	}

	private DbCard getDbCard(final String aUser, final UUID aVocabularyId, final UUID aId) throws PersistException {
		return orm.get(getIndexId(aVocabularyId), aId.toString());
	}

	@Override
	public void deleteCard(final String aUser, final UUID aVocabularyId, final UUID aId) throws PersistException {
		orm.delete(getIndexId(aVocabularyId), aId.toString());
	}

	@Override
	public void deleteAllCards(final String aUser, final UUID aVocabularyId) throws PersistException {
		orm.deleteIndex(getIndexId(aVocabularyId));
	}

	@Override
	public List<Card> queryCards(final String aUser, final Query aQuery) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		final QueryBuilder query = ElasticQueryUtils.asElaticQuery(aQuery);
		searchSourceBuilder.query(query);

		ElasticQueryUtils.addSort(searchSourceBuilder, aQuery.sort());
		ElasticQueryUtils.setFromAndLimit(searchSourceBuilder, aQuery.from(), aQuery.limit());

		final SearchResponse search = orm.search(getIndexId(aQuery.getVocabularyId()), searchSourceBuilder);
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
			Map<String, ?> stats = (Map<String, ?>) source.get("stats");
			stats = stats == null ? Collections.emptyMap() : stats;
			final Map<String, CardStat> statsMap = CardUtils.asCardStats(stats);
			list.add(new PojoCard(id, vocId,
					LanguageUtils.asString(vocabulary.getSource()),
					LanguageUtils.asString(vocabulary.getTarget()),
					map,
					statsMap));
		}
		return list;
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
			content,
			CardUtils.asPersistStats(aCard.getStats()));
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
				map,
				CardUtils.asCardStats(aDbCard.getStats()));
	}

	private Map<String, String> asPersistMap(final Map<String, String> aContent, final List<CardField> aFields) {
		final Map<String, CardField> fieldMap = CardUtils.asFieldsMap(aFields);
		final Map<String, String> map = new HashMap<>();
		for (final Entry<String, String> entry: aContent.entrySet()) {
			map.put(CardUtils.asPersistName(fieldMap.get(entry.getKey())), entry.getValue());
		}
		return map;
	}

}
