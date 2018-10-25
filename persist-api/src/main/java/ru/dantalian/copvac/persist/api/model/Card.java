package ru.dantalian.copvac.persist.api.model;

import java.util.Map;

public interface Card {

	String getId();

	Map<String, CardField> getFields();

	void setFields(Map<String, CardField> aFields);

	void addFields(Map<String, CardField> aFields);

	void addField(Map<String, CardField> aFields);

}
