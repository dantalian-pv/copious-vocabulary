package ru.dantalian.copvoc.persist.elastic.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Field;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Id;
import ru.dantalian.copvoc.persist.elastic.model.codecs.CardFiledTypeCodec;
import ru.dantalian.copvoc.persist.elastic.model.codecs.UUIDCodec;

public class DbCardField implements CardField {

	@Id
	@Field
	private String id;

	@Field(name = "vocabulary_id", codec = UUIDCodec.class)
	private UUID vocabularyId;

	@Field
	private String name;

	@Field(codec = CardFiledTypeCodec.class)
	private CardFiledType	type;

	@Field(type = "integer")
	private Integer order;

	@Field(type = "boolean")
	private boolean system;

	public DbCardField() {
	}

	public DbCardField(final UUID aVocabularyId, final String aName,
			final CardFiledType aType, final Integer aOrder,
			final boolean aSystem) {
		vocabularyId = aVocabularyId;
		name = aName;
		type = aType;
		order = aOrder;
		system = aSystem;
		setId();
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
		setId();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
		setId();
	}

	@Override
	public CardFiledType getType() {
		return type;
	}

	public void setType(final CardFiledType aType) {
		type = aType;
	}

	private void setId() {
		id = (vocabularyId == null ? null : vocabularyId.toString()) + "_" + name;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	public void setOrder(final Integer aOrder) {
		order = aOrder;
	}

	@Override
	public boolean isSystem() {
		return system;
	}

	public void setSystem(final boolean aSystem) {
		system = aSystem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (system ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DbCardField)) {
			return false;
		}
		final DbCardField other = (DbCardField) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (system != other.system) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DbCardField [vocabularyId=" + vocabularyId + ", name=" + name + ", type=" + type
				+ ", order=" + order + ", system=" + system + "]";
	}

}
