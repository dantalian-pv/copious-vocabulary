package ru.dantalian.copvoc.persist.impl.query;

import java.util.Collections;
import java.util.List;

import ru.dantalian.copvoc.persist.api.query.BoolExpression;
import ru.dantalian.copvoc.persist.api.query.QueryExpression;

public class BoolExpressionImpl implements BoolExpression {

	private final List<QueryExpression> must;

	private final List<QueryExpression> not;

	private final List<QueryExpression> should;

	public BoolExpressionImpl(final List<QueryExpression> aMust, final List<QueryExpression> aShould,
			final List<QueryExpression> aNot) {
		must = Collections.unmodifiableList(aMust);
		should = Collections.unmodifiableList(aShould);
		not = Collections.unmodifiableList(aNot);
	}

	@Override
	public List<QueryExpression> must() {
		return must;
	}

	@Override
	public List<QueryExpression> not() {
		return not;
	}

	@Override
	public List<QueryExpression> should() {
		return should;
	}

}
