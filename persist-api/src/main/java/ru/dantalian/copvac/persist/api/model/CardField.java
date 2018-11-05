package ru.dantalian.copvac.persist.api.model;

import java.util.UUID;

public interface CardField {

	UUID getId();

	UUID getBatchId();

	String getName();

	CardFiledType getType();

}
