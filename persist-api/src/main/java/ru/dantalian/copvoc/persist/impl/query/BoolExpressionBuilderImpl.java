package ru.dantalian.copvoc.persist.impl.query;

import java.util.LinkedList;
import java.util.List;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.query.BoolExpression;
import ru.dantalian.copvoc.persist.api.query.BoolExpressionBuilder;
import ru.dantalian.copvoc.persist.api.query.CardsExpression;

public class BoolExpressionBuilderImpl implements BoolExpressionBuilder {

	private List<CardsExpression> must = new LinkedList<>();

	private List<CardsExpression> not = new LinkedList<>();

	private List<CardsExpression> should = new LinkedList<>();

	@Override
	public BoolExpressionBuilder must(final CardsExpression aExpression) {
		must.add(aExpression);
		return this;
	}

	@Override
	public BoolExpressionBuilder should(final CardsExpression aExpression) {
		should.add(aExpression);
		return this;
	}

	@Override
	public BoolExpressionBuilder not(final CardsExpression aExpression) {
		not.add(aExpression);
		return this;
	}

	@Override
	public BoolExpression build() throws PersistException {
		return new BoolExpressionImpl(must, should, not);
	}

}
