package ru.dantalian.copvoc.persist.impl.query;

import java.util.LinkedList;
import java.util.List;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.query.BoolExpression;
import ru.dantalian.copvoc.persist.api.query.BoolExpressionBuilder;
import ru.dantalian.copvoc.persist.api.query.QueryExpression;

public class BoolExpressionBuilderImpl implements BoolExpressionBuilder {

	private List<QueryExpression> must = new LinkedList<>();

	private List<QueryExpression> not = new LinkedList<>();

	private List<QueryExpression> should = new LinkedList<>();

	@Override
	public BoolExpressionBuilder must(final QueryExpression aExpression) {
		must.add(aExpression);
		return this;
	}

	@Override
	public BoolExpressionBuilder should(final QueryExpression aExpression) {
		should.add(aExpression);
		return this;
	}

	@Override
	public BoolExpressionBuilder not(final QueryExpression aExpression) {
		not.add(aExpression);
		return this;
	}

	@Override
	public BoolExpression build() throws PersistException {
		return new BoolExpressionImpl(must, should, not);
	}

}
