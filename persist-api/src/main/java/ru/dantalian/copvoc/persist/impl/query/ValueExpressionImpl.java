package ru.dantalian.copvoc.persist.impl.query;

import ru.dantalian.copvoc.persist.api.query.ValueExpression;

public class ValueExpressionImpl implements ValueExpression {

	private final String name;

	private final Object value;

	public ValueExpressionImpl(final String aName, final Object aValue) {
		name = aName;
		value = aValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
