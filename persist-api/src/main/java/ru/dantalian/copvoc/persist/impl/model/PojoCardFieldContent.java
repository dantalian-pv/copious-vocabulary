package ru.dantalian.copvoc.persist.impl.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardFieldContent;

public class PojoCardFieldContent implements CardFieldContent {

	private UUID cardId;

	private UUID vocabularyId;

	private String fieldName;

	private String content;

	public PojoCardFieldContent() {
	}

	public PojoCardFieldContent(final UUID aCardId, final UUID aVocabularyId, final String aFieldName, final String aContent) {
		cardId = aCardId;
		vocabularyId = aVocabularyId;
		fieldName = aFieldName;
		content = aContent;
	}

	@Override
	public UUID getCardId() {
		return cardId;
	}

	public void setCardId(final UUID aCardId) {
		cardId = aCardId;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(final String aFieldName) {
		fieldName = aFieldName;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(final String aContent) {
		content = aContent;
	}

	@Override
	public String toString() {
		return "PojoCardFieldContent [cardId=" + cardId + ", vocabularyId=" + vocabularyId + ", fieldName=" + fieldName + "]";
	}

}
