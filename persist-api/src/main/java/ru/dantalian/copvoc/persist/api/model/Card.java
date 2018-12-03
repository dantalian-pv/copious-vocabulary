package ru.dantalian.copvoc.persist.api.model;

import java.util.Map;
import java.util.UUID;

public interface Card {

	UUID getId();

	UUID getBatchId();

	Map<UUID, CardFieldContent> getFieldsContent();

	CardFieldContent getContent(UUID aField);

	void setFieldsContent(Map<UUID, CardFieldContent> aFields);

	void addFieldsContent(Map<UUID, CardFieldContent> aFields);

	void addFieldContent(UUID aField, CardFieldContent aContent);

}
