package ru.dantalian.copvac.persist.api.model;

import java.util.UUID;

public interface CardFieldContent {

	UUID getId();

	UUID getBatchId();

	UUID getFieldId();

	String getContent();

}
