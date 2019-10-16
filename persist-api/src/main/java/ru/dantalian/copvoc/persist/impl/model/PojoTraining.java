package ru.dantalian.copvoc.persist.impl.model;

import java.util.List;
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

	private List<UUID> cards;

	private int cardIndex;

	public PojoTraining() {
	}

	public PojoTraining(final UUID aId, final UUID aVocabularyId, final Map<String, CardStat> aStats,
			final int aSize, final List<UUID> aCards, final int aCardIndex) {
		id = aId;
		vocabularyId = aVocabularyId;
		stats = aStats;
		size = aSize;
		cards = aCards;
		cardIndex = aCardIndex;
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

	public void setSize(final int aSize) {
		size = aSize;
	}

	@Override
	public List<UUID> getCards() {
		return cards;
	}

	public void setCards(final List<UUID> aCards) {
		cards = aCards;
	}

	@Override
	public int getCardIndex() {
		return cardIndex;
	}

	public void setCardIndex(final int aCardIndex) {
		cardIndex = aCardIndex;
	}

	@Override
	public String toString() {
		return "PojoTraining [id=" + id + ", vocabularyId=" + vocabularyId
				+ ", stats=" + stats + ", finished=" + finished + ", size=" + size + "]";
	}

}
