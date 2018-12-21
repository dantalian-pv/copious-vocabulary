package ru.dantalian.copvoc.persist.impl.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;
import ru.dantalian.copvoc.persist.api.query.CardsQueryBuilder;
import ru.dantalian.copvoc.persist.api.utils.Validator;

public class DefaultCardsQueryBuilder implements CardsQueryBuilder {

	private UUID vocabularyId;

	@Override
	public CardsQueryBuilder setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
		return this;
	}

	@Override
	public CardsQuery build() throws PersistException {
		vocabularyId = Validator.checkNotNull(vocabularyId, "vocabularyId cannot be null");
		return new DefaultCardsQuery(vocabularyId);
	}

}
