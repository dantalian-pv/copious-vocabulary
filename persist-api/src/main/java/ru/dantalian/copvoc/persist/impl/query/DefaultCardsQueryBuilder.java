package ru.dantalian.copvoc.persist.impl.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;
import ru.dantalian.copvoc.persist.api.query.CardsQueryBuilder;
import ru.dantalian.copvoc.persist.api.utils.Validator;

public class DefaultCardsQueryBuilder implements CardsQueryBuilder {

	private UUID batchId;

	@Override
	public CardsQueryBuilder setBatchId(final UUID aBatchId) {
		batchId = aBatchId;
		return this;
	}

	@Override
	public CardsQuery build() throws PersistException {
		batchId = Validator.checkNotNull(batchId, "batchId cannot be null");
		return new DefaultCardsQuery(batchId);
	}

}
