package ru.dantalian.copvoc.persist.elastic.model.codecs;

import java.util.UUID;

import ru.dantalian.copvoc.persist.elastic.model.DbTrainingCardStatsId;

public class DbTrainingCardStatsIdCodec extends KnownTypeCodec<DbTrainingCardStatsId, String> {

	public DbTrainingCardStatsIdCodec() {
		super(DbTrainingCardStatsId.class, String.class);
	}

	@Override
	public String serialize(final DbTrainingCardStatsId aEntry) throws CodecException {
		if (aEntry == null) {
			return null;
		}
		return aEntry.getTrainingId().toString() + ":" + aEntry.getCardId().toString();
	}

	@Override
	public DbTrainingCardStatsId deserialize(final String aEntry) throws CodecException {
		if (aEntry == null) {
			return null;
		}
		final String[] split = aEntry.split(":");
		if (split.length != 2) {
			throw new IllegalArgumentException("Wrong id format in " + aEntry);
		}
		return new DbTrainingCardStatsId(UUID.fromString(split[0]), UUID.fromString(split[1]));
	}

}
