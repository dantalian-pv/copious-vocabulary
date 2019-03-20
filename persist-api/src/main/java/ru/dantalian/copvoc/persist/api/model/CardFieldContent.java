package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface CardFieldContent {

	UUID getCardId();

	UUID getVocabularyId();

	String getFieldName();

	String getContent();

	void setContent(String aContent);

}
