package ru.dantalian.copvoc.persist.impl.query;

import ru.dantalian.copvoc.persist.api.query.CardsQueryBuilder;

public class QueryFactory {

	private QueryFactory() {
	}

	public static CardsQueryBuilder newCardsQuery() {
		return new DefaultCardsQueryBuilder();
	}

}
