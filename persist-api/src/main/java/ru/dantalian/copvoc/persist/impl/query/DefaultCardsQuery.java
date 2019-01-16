package ru.dantalian.copvoc.persist.impl.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.query.CardsExpression;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;

public class DefaultCardsQuery implements CardsQuery {

	private final UUID vocabularyId;

	private final CardsExpression expression;

	public DefaultCardsQuery(final UUID aVocabularyId, final CardsExpression aExpression) {
		vocabularyId = aVocabularyId;
		expression = aExpression;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	@Override
	public CardsExpression where() {
		return expression;
	}

}
