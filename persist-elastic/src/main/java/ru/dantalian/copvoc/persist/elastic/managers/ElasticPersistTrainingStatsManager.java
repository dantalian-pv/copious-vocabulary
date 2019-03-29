package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;
import ru.dantalian.copvoc.persist.elastic.model.DbTrainingCardStats;
import ru.dantalian.copvoc.persist.elastic.model.DbTrainingCardStatsId;
import ru.dantalian.copvoc.persist.elastic.model.codecs.CodecException;
import ru.dantalian.copvoc.persist.elastic.model.codecs.DbTrainingCardStatsIdCodec;
import ru.dantalian.copvoc.persist.elastic.utils.CardUtils;

@Service
public class ElasticPersistTrainingStatsManager extends AbstractPersistManager<DbTrainingCardStats> {

	private static final String DEFAULT_INDEX = "training_stats";

	private final ElasticSettings settings;

	private final DbTrainingCardStatsIdCodec idCodec;

	public ElasticPersistTrainingStatsManager(final RestHighLevelClient aClient, final ElasticSettings aSettings) {
		super(aClient, DbTrainingCardStats.class);
		settings = aSettings;
		idCodec = new DbTrainingCardStatsIdCodec();
	}

	public void updateStats(final String aUser, final UUID aTrainigId, final UUID aCardId,
			final Map<String, CardStat> aStats) throws PersistException {
		try {
			final Map<String, Object> persistStats = CardUtils.asPersistStats(aStats);
			final DbTrainingCardStatsId statsId = new DbTrainingCardStatsId(aTrainigId, aCardId);
			DbTrainingCardStats stats = get(getDefaultIndex(),
					idCodec.serialize(statsId));
			if (stats == null) {
				stats = new DbTrainingCardStats(statsId, persistStats);
				add(getDefaultIndex(), stats, false);
			} else {
				final Map<String, Object> newPersistStats = new HashMap<>(stats.getStats());
				newPersistStats.putAll(persistStats);
				stats.setStats(newPersistStats);
				update(getDefaultIndex(), stats, false);
			}
		} catch (final CodecException e) {
			throw new PersistException("Faield to update stats", e);
		}
	}

	public Map<String, CardStat> getStats(final String aUser, final UUID aTrainigId, final UUID aCardId)
			throws PersistException {
		try {
			final DbTrainingCardStatsId statsId = new DbTrainingCardStatsId(aTrainigId, aCardId);
			final DbTrainingCardStats stats = get(getDefaultIndex(), idCodec.serialize(statsId));
			return CardUtils.asCardStats(stats.getStats());
		} catch (final CodecException e) {
			throw new PersistException("Faield to get stats", e);
		}
	}

	@Override
	protected String getDefaultIndex() {
		return DEFAULT_INDEX;
	}

	@Override
	protected XContentBuilder getSettings(final String aIndex) throws PersistException {
		return settings.getDefaultSettings();
	}

}
