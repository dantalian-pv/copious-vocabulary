package ru.dantalian.copvoc.persist.sqlite.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public class DbCardField implements CardField {

	private UUID id;

	private UUID batchId;

	private String name;

	private CardFiledType	type;

	public DbCardField() {
	}

	public DbCardField(final UUID aId, final UUID aBatchId, final String aName,
			final CardFiledType aType) {
		id = aId;
		batchId = aBatchId;
		name = aName;
		type = aType;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	@Override
	public UUID getBatchId() {
		return batchId;
	}

	public void setBatchId(final UUID aBatchId) {
		batchId = aBatchId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	@Override
	public CardFiledType getType() {
		return type;
	}

	public void setType(final CardFiledType aType) {
		type = aType;
	}

	@Override
	public String toString() {
		return "DbCardField [id=" + id + ", batchId=" + batchId + ", name=" + name + ", type=" + type + "]";
	}

}
