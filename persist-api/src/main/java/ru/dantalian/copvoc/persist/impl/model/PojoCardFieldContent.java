package ru.dantalian.copvoc.persist.impl.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardFieldContent;

public class PojoCardFieldContent implements CardFieldContent {

	private UUID cardId;

	private UUID batchId;

	private String fieldName;

	private String content;

	public PojoCardFieldContent() {
	}

	public PojoCardFieldContent(final UUID aCardId, final UUID aBatchId, final String aFieldName, final String aContent) {
		cardId = aCardId;
		batchId = aBatchId;
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
	public UUID getBatchId() {
		return batchId;
	}

	public void setBatchId(final UUID aBatchId) {
		batchId = aBatchId;
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

	public void setContent(final String aContent) {
		content = aContent;
	}

	@Override
	public String toString() {
		return "PojoCardFieldContent [cardId=" + cardId + ", batchId=" + batchId + ", fieldName=" + fieldName + "]";
	}

}
