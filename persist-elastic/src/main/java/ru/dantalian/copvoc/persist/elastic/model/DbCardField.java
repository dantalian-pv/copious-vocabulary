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

	@Field(codec = UUIDCodec.class)
	private UUID vocabularyId;

	@Field
	private String name;

	@Field(codec = CardFiledTypeCodec.class)
	private CardFiledType	type;

	public DbCardField() {
	}

	public DbCardField(final UUID aVocabularyId, final String aName,
			final CardFiledType aType) {
		vocabularyId = aVocabularyId;
		name = aName;
		type = aType;
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
	public String toString() {
		return "DbCardField [vocabularyId=" + vocabularyId + ", name=" + name + ", type=" + type + "]";
	}

}
