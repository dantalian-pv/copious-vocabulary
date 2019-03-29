package ru.dantalian.copvoc.persist.impl.model;

import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.Training;

public class PojoTraining implements Training {

	private UUID id;

	private UUID vocabularyId;

	private Map<String, CardStat> stats;

	private boolean finished;

	private int size;

	public PojoTraining() {
	}

	public PojoTraining(final UUID aId, final UUID aVocabularyId, final Map<String, CardStat> aStats,
			final int aSize) {
		id = aId;
		vocabularyId = aVocabularyId;
		stats = aStats;
		size = aSize;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	@Override
	public Map<String, CardStat> getStats() {
		return stats;
	}

	public void setStats(final Map<String, CardStat> aStats) {
		stats = aStats;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(final boolean aFinished) {
		finished = aFinished;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "PojoTraining [id=" + id + ", vocabularyId=" + vocabularyId
				+ ", stats=" + stats + ", finished=" + finished + ", size=" + size + "]";
	}

}
