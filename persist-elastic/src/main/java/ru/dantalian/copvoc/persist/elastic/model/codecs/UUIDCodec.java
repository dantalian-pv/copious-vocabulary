package ru.dantalian.copvoc.persist.elastic.model.codecs;

import java.util.UUID;

public class UUIDCodec implements FieldCodec<UUID, String> {

	@Override
	public String serialize(final UUID aEntry) {
		return aEntry == null ? null : aEntry.toString();
	}

	@Override
	public UUID deserialize(final String aEntry) {
		return aEntry == null ? null : UUID.fromString(aEntry);
	}

}
