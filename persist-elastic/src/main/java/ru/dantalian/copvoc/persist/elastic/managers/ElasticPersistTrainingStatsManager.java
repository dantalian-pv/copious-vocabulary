package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.elastic.model.DbTrainingCardStats;
import ru.dantalian.copvoc.persist.elastic.model.DbTrainingCardStatsId;
import ru.dantalian.copvoc.persist.elastic.model.codecs.CodecException;
import ru.dantalian.copvoc.persist.elastic.model.codecs.DbTrainingCardStatsIdCodec;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORM;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORMFactory;
import ru.dantalian.copvoc.persist.elastic.utils.CardUtils;
import ru.dantalian.copvoc.persist.elastic.utils.ElasticQueryUtils;

@Service
public class ElasticPersistTrainingStatsManager {

	private static final String DEFAULT_INDEX = "training_stats";

	@Autowired
	private DefaultSettingsProvider settingsProvider;

	@Autowired
	private ElasticORMFactory ormFactory;

	private ElasticORM<DbTrainingCardStats> orm;

	private DbTrainingCardStatsIdCodec idCodec;

	@PostConstruct
	public void init() {
		orm = ormFactory.newElasticORM(DbTrainingCardStats.class, settingsProvider);
		idCodec = new DbTrainingCardStatsIdCodec();
	}

	public void updateStats(final String aUser, final UUID aTrainigId, final UUID aCardId,
			final Map<String, CardStat> aStats) throws PersistException {
		try {
			final Map<String, Object> persistStats = CardUtils.asPersistStats(aStats);
			final DbTrainingCardStatsId statsId = new DbTrainingCardStatsId(aTrainigId, aCardId);
			DbTrainingCardStats stats = orm.get(DEFAULT_INDEX,
					idCodec.serialize(statsId));
			if (stats == null) {
				stats = new DbTrainingCardStats(statsId, persistStats);
				orm.add(DEFAULT_INDEX, stats, false);
			} else {
				final Map<String, Object> newPersistStats = new HashMap<>(stats.getStats());
				newPersistStats.putAll(persistStats);
				stats.setStats(newPersistStats);
				orm.update(DEFAULT_INDEX, stats, false);
			}
		} catch (final CodecException e) {
			throw new PersistException("Faield to update stats", e);
		}
	}

	public void updateStatForCard(final String aUser, final UUID aTrainigId, final UUID aCardId,
			final CardStatAction aAction) throws PersistException {
		try {
			final DbTrainingCardStatsId statsId = new DbTrainingCardStatsId(aTrainigId, aCardId);
			orm.updateByScript(DEFAULT_INDEX, idCodec.serialize(statsId),
					ElasticQueryUtils.asElasticScript(aAction), false);
		} catch (final CodecException e) {
			throw new PersistException("Faield to update stats for card", e);
		}
	}

	public Map<String, CardStat> getStats(final String aUser, final UUID aTrainigId, final UUID aCardId)
			throws PersistException {
		try {
			final DbTrainingCardStatsId statsId = new DbTrainingCardStatsId(aTrainigId, aCardId);
			final DbTrainingCardStats stats = orm.get(DEFAULT_INDEX, idCodec.serialize(statsId));
			return CardUtils.asCardStats(stats.getStats());
		} catch (final CodecException e) {
			throw new PersistException("Faield to get stats", e);
		}
	}

}
