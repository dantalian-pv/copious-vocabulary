package ru.dantalian.copvoc.persist.sqlite.model;

import java.util.Map;
import java.util.UUID;

public class DbCard {

	private UUID id;

	private UUID batchId;

	private Map<String, String> fieldsContent;

	public DbCard(final UUID aId, final UUID aBatchId, final Map<String, String> aFieldsContent) {
		id = aId;
		batchId = aBatchId;
		fieldsContent = aFieldsContent;
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

	public Map<String, String> getFieldsContent() {
		return fieldsContent;
	}

	public void setFieldsContent(final Map<String, String> aFieldsContent) {
		fieldsContent = aFieldsContent;
	}

}
