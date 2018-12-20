package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface CardField {

	UUID getBatchId();

	String getName();

	CardFiledType getType();

}
