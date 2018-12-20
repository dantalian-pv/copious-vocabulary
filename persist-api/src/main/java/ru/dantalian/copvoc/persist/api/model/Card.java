package ru.dantalian.copvoc.persist.api.model;

import java.util.Map;
import java.util.UUID;

public interface Card {

	UUID getId();

	UUID getBatchId();

	Map<String, CardFieldContent> getFieldsContent();

	CardFieldContent getContent(String aFieldName);

	void setFieldsContent(Map<String, CardFieldContent> aFields);

	void addFieldsContent(Map<String, CardFieldContent> aFields);

	void addFieldContent(String aFieldName, CardFieldContent aContent);

}
