package ru.dantalian.copvoc.persist.elastic.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.elastic.model.annotations.Field;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Id;
import ru.dantalian.copvoc.persist.elastic.model.annotations.SubField;
import ru.dantalian.copvoc.persist.elastic.model.codecs.UUIDCodec;

public class DbTraining {

	@Id
	@Field(codec = UUIDCodec.class)
	private UUID id;

	@Field(name = "vocabulary_id", codec = UUIDCodec.class)
	private UUID vocabularyId;

	@Field(name = "stats", type = "object", subtype = {
			@SubField(path_match="stats.*_long", type = "long"),
			@SubField(path_match="stats.*_double", type = "double"),
			@SubField(path_match="stats.*_date", type = "date")
	})
	private Map<String, Object> stats;

	@Field
	private boolean finished;

	@Field(name = "cards", index = false, type = "keyword")
	private List<String> cards;

	@Field(name = "card_index", index = false, type = "integer")
	private int cardIndex;

	public DbTraining() {
	}

	public DbTraining(final UUID aId, final UUID aVocabularyId,
			final List<String> aCards,
			final Map<String, Object> aStats) {
		id = aId;
		vocabularyId = aVocabularyId;
		cards = aCards;
		stats = aStats;
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	public Map<String, Object> getStats() {
		return stats;
	}

	public void setStats(final Map<String, Object> aStats) {
		stats = aStats;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(final boolean aFinished) {
		finished = aFinished;
	}

	public List<String> getCards() {
		return cards;
	}

	public void setCards(final List<String> aCards) {
		cards = aCards;
	}

	public int getCardIndex() {
		return cardIndex;
	}

	public void setCardIndex(final int aCardIndex) {
		cardIndex = aCardIndex;
	}

}
