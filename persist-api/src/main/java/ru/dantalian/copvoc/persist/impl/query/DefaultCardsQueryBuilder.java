package ru.dantalian.copvoc.persist.impl.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.query.CardsExpression;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;
import ru.dantalian.copvoc.persist.api.query.CardsQueryBuilder;

public class DefaultCardsQueryBuilder implements CardsQueryBuilder {

	private UUID vocabularyId;
	private CardsExpression expression;

	@Override
	public CardsQueryBuilder setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
		return this;
	}

	@Override
	public CardsQueryBuilder where(final CardsExpression aExpression) {
		expression = aExpression;
		return this;
	}

	@Override
	public CardsQuery build() throws PersistException {
		return new DefaultCardsQuery(vocabularyId, expression);
	}



}
