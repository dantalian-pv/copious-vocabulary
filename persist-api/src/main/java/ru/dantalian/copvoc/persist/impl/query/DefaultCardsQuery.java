package ru.dantalian.copvoc.persist.impl.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.query.CardsQuery;

public class DefaultCardsQuery implements CardsQuery {

	private final UUID batchId;

	public DefaultCardsQuery(final UUID aBatchId) {
		batchId = aBatchId;
	}

	@Override
	public UUID getBatchId() {
		return batchId;
	}

}
