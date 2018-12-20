package ru.dantalian.copvoc.persist.impl.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;

public class PojoCard implements Card {

	private UUID id;

	private UUID batchId;

	private Map<String, CardFieldContent> fieldsContent;

	public PojoCard() {
	}

	public PojoCard(final UUID aId, final UUID aBatchId, final Map<String, CardFieldContent> aFieldsContent) {
		id = aId;
		batchId = aBatchId;
		fieldsContent = aFieldsContent;
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
	public Map<String, CardFieldContent> getFieldsContent() {
		return fieldsContent == null ? Collections.emptyMap() : fieldsContent;
	}

	@Override
	public void setFieldsContent(final Map<String, CardFieldContent> aFieldsContent) {
		fieldsContent = aFieldsContent;
	}

	@Override
	public CardFieldContent getContent(final String aFieldName) {
		return fieldsContent.get(aFieldName);
	}

	@Override
	public void addFieldsContent(final Map<String, CardFieldContent> aFields) {
		if (fieldsContent == null) {
			fieldsContent = new HashMap<>();
		}
		fieldsContent.putAll(aFields);
	}

	@Override
	public void addFieldContent(final String aField, final CardFieldContent aContent) {
		if (fieldsContent == null) {
			fieldsContent = new HashMap<>();
		}
		fieldsContent.put(aField, aContent);
	}

	@Override
	public String toString() {
		return "PojoCard [id=" + id + ", batchId=" + batchId + ", fieldsContent=" + fieldsContent + "]";
	}

}
