package ru.dantalian.copvoc.persist.impl.model.personal;

import java.util.UUID;

public class PojoCardFieldContent {

	private UUID id;

	private UUID batchId;

	private UUID fieldId;

	private String content;

	public PojoCardFieldContent() {
	}

	public PojoCardFieldContent(final UUID aId, final UUID aBatchId, final UUID aFieldId, final String aContent) {
		id = aId;
		batchId = aBatchId;
		fieldId = aFieldId;
		content = aContent;
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	public UUID getBatchId() {
		return batchId;
	}

	public void setBatchId(final UUID aBatchId) {
		batchId = aBatchId;
	}

	public UUID getFieldId() {
		return fieldId;
	}

	public void setFieldId(final UUID aFieldId) {
		fieldId = aFieldId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String aContent) {
		content = aContent;
	}

	@Override
	public String toString() {
		return "PojoCardFieldContent [id=" + id + ", batchId=" + batchId + ", fieldId=" + fieldId
				+ ", content=" + content + "]";
	}

}
