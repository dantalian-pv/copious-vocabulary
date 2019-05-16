package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.EmptyResultPersistException;
import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistTrainingManager;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.api.model.Training;
import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.query.QueryFactory;
import ru.dantalian.copvoc.persist.elastic.model.DbTraining;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORM;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORMFactory;
import ru.dantalian.copvoc.persist.elastic.utils.CardUtils;
import ru.dantalian.copvoc.persist.elastic.utils.ElasticQueryUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoTraining;

@Service
public class ElasticPersistTrainingManager implements PersistTrainingManager {

	private static final String DEFAULT_INDEX = "trainings";

	@Autowired
	private ElasticPersistTrainingStatsManager statsManager;

	@Autowired
	private PersistCardManager cardManager;

	@Autowired
	private DefaultSettingsProvider settingsProvider;

	@Autowired
	private ElasticORMFactory ormFactory;

	private ElasticORM<DbTraining> orm;

	@PostConstruct
	public void init() {
		orm = ormFactory.newElasticORM(DbTraining.class, settingsProvider);
	}

	@Override
	public Training createTraining(final String aUser, final UUID aVocabularyId, final Optional<Integer> aCount,
			final Map<String, CardStat> aStatsMap) throws PersistException {
		final UUID id = UUID.randomUUID();
		final Integer count = aCount.orElse(20);
		final ru.dantalian.copvoc.persist.api.query.QueryBuilder cardsQuery = QueryFactory.newCardsQuery();
		cardsQuery.setVocabularyId(aVocabularyId);
		cardsQuery.addSort(QueryFactory.sortRandom());
		cardsQuery.limit(count);

		final List<Card> cards = cardManager.queryCards(aUser, cardsQuery.build()).getItems();
		if (cards.isEmpty()) {
			throw new EmptyResultPersistException("No cards found");
		}
		for (final Card card: cards) {
			statsManager.updateStats(aUser, id, card.getId(), aStatsMap);
		}
		final List<String> cardIds = cards.stream()
			.map(aItem -> aItem.getId().toString())
			.collect(Collectors.toList());
		final DbTraining training = new DbTraining(id, aVocabularyId, cardIds, CardUtils.asPersistStats(aStatsMap));
		orm.add(DEFAULT_INDEX, training, true);
		return asTraining(training);
	}

	@Override
	public Training getTraining(final String aUser, final UUID aTrainigId) throws PersistException {
		final DbTraining dbTraining = getDbTraining(aTrainigId);
		return asTraining(dbTraining);
	}

	@Override
	public List<Training> queryTrainings(final String aUser, final Query aQuery) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		final QueryBuilder query = ElasticQueryUtils.asElaticQuery(aQuery);
		searchSourceBuilder.query(query);

		ElasticQueryUtils.addSort(searchSourceBuilder, aQuery.sort());
		ElasticQueryUtils.setFromAndLimit(searchSourceBuilder, aQuery.from(), aQuery.limit());

		final SearchResponse search = orm.search(DEFAULT_INDEX, searchSourceBuilder);
		final List<Training> list = new LinkedList<>();
		final Iterator<SearchHit> iterator = search.getHits().iterator();
		while(iterator.hasNext()) {
			final SearchHit hit = iterator.next();
			final Map<String, Object> source = hit.getSourceAsMap();
			final UUID id = UUID.fromString(hit.getId());
			final UUID vocId = UUID.fromString((String) source.get("vocabulary_id"));
			final List<String> cards = (List<String>) source.get("cards");

			Map<String, Object> stats = (Map<String, Object>) source.get("stats");
			stats = stats == null ? Collections.emptyMap() : stats;

			final DbTraining training = new DbTraining(id, vocId, cards, stats);
			list.add(asTraining(training));
		}
		return list;
	}

	@Override
	public void updateStatForCard(final String aUser, final UUID aTrainigId, final UUID aCardId,
			final CardStatAction aAction) throws PersistException {
		final DbTraining dbTraining = getDbTraining(aTrainigId);
		try {
			CompletableFuture.allOf(
				CompletableFuture.runAsync(() -> {
					try {
						cardManager.updateStatForCard(aUser, dbTraining.getVocabularyId(), aCardId, aAction);
					} catch (final PersistException e) {
						throw new CompletionException(e);
					}
				}),
				CompletableFuture.runAsync(() -> {
					try {
						statsManager.updateStatForCard(aUser, aTrainigId, aCardId, aAction);
					} catch (final PersistException e) {
						throw new CompletionException(e);
					}
				}),
				CompletableFuture.runAsync(() -> {
					try {
						orm.updateByScript(DEFAULT_INDEX, aTrainigId.toString(),
								ElasticQueryUtils.asElasticScript(aAction), false);
					} catch (final PersistException e) {
						throw new CompletionException(e);
					}
				})
			).join();
		} catch (final CompletionException e) {
			throw (PersistException) e.getCause();
		}
	}

	private DbTraining getDbTraining(final UUID aTrainigId) throws PersistException {
		final DbTraining dbTraining = orm.get(DEFAULT_INDEX, aTrainigId.toString());
		return dbTraining;
	}

	@Override
	public Training finishTraining(final String aUser, final UUID aTrainigId) throws PersistException {
		final DbTraining dbTraining = getDbTraining(aTrainigId);
		if (dbTraining == null) {
			throw new PersistException("training not found id: " + aTrainigId);
		}
		dbTraining.setFinished(true);
		orm.update(DEFAULT_INDEX, dbTraining, true);
		return asTraining(dbTraining);
	}

	@Override
	public UUID firstCard(final String aUser, final UUID aTrainigId) throws PersistException {
		final DbTraining training = orm.get(DEFAULT_INDEX, aTrainigId.toString());
		if (training == null) {
			throw new PersistException("No training found id: " + aTrainigId);
		}
		return UUID.fromString(training.getCards().get(0));
	}

	@Override
	public UUID nextCard(final String aUser, final UUID aTrainigId, final UUID aCardId) throws PersistException {
		final DbTraining training = orm.get(DEFAULT_INDEX, aTrainigId.toString());
		if (training == null) {
			throw new PersistException("No training found id: " + aTrainigId);
		}
		final String current = aCardId.toString();
		String id = null;
		final Iterator<String> iterator = training.getCards().iterator();
		while (iterator.hasNext()) {
			final String cardId = iterator.next();
			if (cardId.equals(current) && iterator.hasNext()) {
				id = iterator.next();
				break;
			}
		}
		return id == null ? null : UUID.fromString(id);
	}

	@Override
	public Map<String, CardStat> getStatsForCard(final String aUser, final UUID aTrainigId, final UUID aCardId) throws PersistException {
		return statsManager.getStats(aUser, aTrainigId, aCardId);
	}

	@Override
	public void updateStatsForCard(final String aUser, final UUID aTrainigId, final UUID aCardId, final Map<String, CardStat> aStats)
			throws PersistException {
		statsManager.updateStats(aUser, aTrainigId, aCardId, aStats);
	}

	private Training asTraining(final DbTraining aTraining) {
		if (aTraining == null) {
			return null;
		}
		return new PojoTraining(aTraining.getId(), aTraining.getVocabularyId(),
				CardUtils.asCardStats(aTraining.getStats()), aTraining.getCards().size());
	}

}
