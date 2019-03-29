package ru.dantalian.copvoc.persist.elastic.model;

import java.util.Map;

import ru.dantalian.copvoc.persist.elastic.model.annotations.Field;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Id;
import ru.dantalian.copvoc.persist.elastic.model.annotations.SubField;
import ru.dantalian.copvoc.persist.elastic.model.codecs.DbTrainingCardStatsIdCodec;

public class DbTrainingCardStats {

	@Id
	@Field(codec = DbTrainingCardStatsIdCodec.class)
	private DbTrainingCardStatsId id;

	@Field(name = "stats", type = "object", subtype = {
			@SubField(path_match="stats.*_long", type = "long"),
			@SubField(path_match="stats.*_double", type = "double"),
			@SubField(path_match="stats.*_date", type = "date")
	})
	private Map<String, Object> stats;

	public DbTrainingCardStats() {
	}

	public DbTrainingCardStats(final DbTrainingCardStatsId aId, final Map<String, Object> aStats) {
		id = aId;
		stats = aStats;
	}

	public DbTrainingCardStatsId getId() {
		return id;
	}

	public void setId(final DbTrainingCardStatsId aId) {
		id = aId;
	}

	public Map<String, Object> getStats() {
		return stats;
	}

	public void setStats(final Map<String, Object> aStats) {
		stats = aStats;
	}

}
