package ru.dantalian.copvoc.persist.impl.query;

import java.util.Collections;
import java.util.List;

import ru.dantalian.copvoc.persist.api.query.BoolExpression;
import ru.dantalian.copvoc.persist.api.query.CardsExpression;

public class BoolExpressionImpl implements BoolExpression {

	private final List<CardsExpression> must;

	private final List<CardsExpression> not;

	private final List<CardsExpression> should;

	public BoolExpressionImpl(final List<CardsExpression> aMust, final List<CardsExpression> aShould,
			final List<CardsExpression> aNot) {
		must = Collections.unmodifiableList(aMust);
		should = Collections.unmodifiableList(aShould);
		not = Collections.unmodifiableList(aNot);
	}

	@Override
	public List<CardsExpression> must() {
		return must;
	}

	@Override
	public List<CardsExpression> not() {
		return not;
	}

	@Override
	public List<CardsExpression> should() {
		return should;
	}

}
