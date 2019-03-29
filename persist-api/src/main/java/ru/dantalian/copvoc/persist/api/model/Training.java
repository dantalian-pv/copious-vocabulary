package ru.dantalian.copvoc.persist.api.model;

import java.util.Map;
import java.util.UUID;

public interface Training {

	UUID getId();

	UUID getVocabularyId();

	boolean isFinished();

	Map<String, CardStat> getStats();

	int getSize();

}
