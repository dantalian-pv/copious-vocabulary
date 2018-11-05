package ru.dantalian.copvac.persist.api.model;

import java.util.Map;
import java.util.UUID;

public interface Card {

	UUID getId();

	String getBatchId();

	Map<String, CardField> getFields();

	void setFields(Map<String, CardField> aFields);

	void addFields(Map<String, CardField> aFields);

	void addField(Map<String, CardField> aFields);

}
