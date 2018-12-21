package ru.dantalian.copvoc.persist.impl.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.query.CardsQuery;

public class DefaultCardsQuery implements CardsQuery {

	private final UUID vocabularyId;

	public DefaultCardsQuery(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

}
