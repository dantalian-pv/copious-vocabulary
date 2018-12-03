package ru.dantalian.copvoc.persist.impl.model.personal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;

public class PojoCard implements Card {

	private UUID id;

	private UUID batchId;

	private Map<UUID, CardFieldContent> fieldsContent;

	public PojoCard() {
	}

	public PojoCard(final UUID aId, final UUID aBatchId, final Map<UUID, CardFieldContent> aFieldsContent) {
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
	public Map<UUID, CardFieldContent> getFieldsContent() {
		return fieldsContent == null ? Collections.emptyMap() : fieldsContent;
	}

	@Override
	public void setFieldsContent(final Map<UUID, CardFieldContent> aFieldsContent) {
		fieldsContent = aFieldsContent;
	}

	@Override
	public CardFieldContent getContent(final UUID aField) {
		return fieldsContent.get(aField);
	}

	@Override
	public void addFieldsContent(final Map<UUID, CardFieldContent> aFields) {
		if (fieldsContent == null) {
			fieldsContent = new HashMap<>();
		}
		fieldsContent.putAll(aFields);
	}

	@Override
	public void addFieldContent(final UUID aField, final CardFieldContent aContent) {
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
