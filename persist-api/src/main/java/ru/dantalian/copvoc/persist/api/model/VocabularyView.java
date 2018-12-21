package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface VocabularyView {

	UUID getVocabularyId();

	String getCss();

	String getFront();

	String getBack();

}
