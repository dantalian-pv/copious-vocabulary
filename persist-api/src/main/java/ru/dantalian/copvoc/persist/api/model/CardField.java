package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface CardField {

	UUID getId();

	UUID getBatchId();

	String getName();

	String getDisplayName();

	CardFiledType getType();

}
