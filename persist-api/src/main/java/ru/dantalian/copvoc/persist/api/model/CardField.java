package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface CardField {

	UUID getVocabularyId();

	String getName();

	CardFiledType getType();

	Integer getOrder();

	boolean isSystem();

}
