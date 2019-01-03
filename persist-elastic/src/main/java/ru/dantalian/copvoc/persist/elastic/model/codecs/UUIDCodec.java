package ru.dantalian.copvoc.persist.elastic.model.codecs;

import java.util.UUID;

public class UUIDCodec extends KnownTypeCodec<UUID, String> {

	public UUIDCodec(final Class<UUID> aTypeClass, final Class<String> aSerializedClass) {
		super(UUID.class, String.class);
	}

}
