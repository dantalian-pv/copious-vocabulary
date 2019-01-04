package ru.dantalian.copvoc.persist.elastic.model.codecs;

import java.util.UUID;

public class UUIDCodec extends KnownTypeCodec<UUID, String> {

	public UUIDCodec() {
		super(UUID.class, String.class);
	}

	@Override
	public String serialize(final UUID aEntry) throws CodecException {
		if (aEntry == null) {
			return null;
		}
		return aEntry.toString();
	}

	@Override
	public UUID deserialize(final String aEntry) throws CodecException {
		if (aEntry == null) {
			return null;
		}
		return UUID.fromString(aEntry);
	}

}
