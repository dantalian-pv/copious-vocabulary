package ru.dantalian.copvoc.persist.impl.query;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.query.ValueCardFieldExpression;

public class ValueCardFieldExpressionImpl implements ValueCardFieldExpression {

	private final CardField field;

	private final Object value;

	public ValueCardFieldExpressionImpl(final CardField aField, final Object aValue) {
		field = aField;
		value = aValue;
	}

	@Override
	public CardField getField() {
		return field;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
