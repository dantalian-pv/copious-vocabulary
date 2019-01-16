package ru.dantalian.copvoc.persist.impl.query;

import ru.dantalian.copvoc.persist.api.query.CardsQueryBuilder;
import ru.dantalian.copvoc.persist.api.query.TermExpression;

public class QueryFactory {

	private QueryFactory() {
	}

	public static CardsQueryBuilder newCardsQuery() {
		return new DefaultCardsQueryBuilder();
	}

	public static TermExpression term(final String aKey, final String aValue, final boolean aWildcard) {
		return new TermExpressionImpl(aKey, aValue, aWildcard);
	}

}
