package ru.dantalian.copvoc.web.controllers.rest.model;

import java.util.Map;

public class DtoTrainingStats {

	private String id;

	private String vocabularyId;

	private Map<String, Object> stats;

	private int size;

	private boolean finished;

	public DtoTrainingStats() {
	}

	public DtoTrainingStats(final String aId, final String aVocabularyId, final Map<String, Object> aStats, final int aSize,
			final boolean aFinished) {
		id = aId;
		vocabularyId = aVocabularyId;
		stats = aStats;
		size = aSize;
		finished = aFinished;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final String aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	public Map<String, Object> getStats() {
		return stats;
	}

	public void setStats(final Map<String, Object> aStats) {
		stats = aStats;
	}

	public int getSize() {
		return size;
	}

	public void setSize(final int aSize) {
		size = aSize;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(final boolean aFinished) {
		finished = aFinished;
	}

}
