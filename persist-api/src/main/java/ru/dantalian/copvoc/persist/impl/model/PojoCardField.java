package ru.dantalian.copvoc.persist.impl.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public class PojoCardField implements CardField {

	private UUID vocabularyId;

	private String name;

	private CardFiledType	type;

	public PojoCardField() {
	}

	public PojoCardField(final UUID aVocabularyId, final String aName,
			final CardFiledType aType) {
		vocabularyId = aVocabularyId;
		name = aName;
		type = aType;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
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
		return "PojoCardField [vocabularyId=" + vocabularyId + ", name=" + name + ", type=" + type + "]";
	}

}
