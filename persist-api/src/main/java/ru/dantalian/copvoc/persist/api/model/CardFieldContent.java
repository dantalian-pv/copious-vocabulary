package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface CardFieldContent {

	UUID getCardId();

	UUID getBatchId();

	String getFieldName();

	String getContent();

}
