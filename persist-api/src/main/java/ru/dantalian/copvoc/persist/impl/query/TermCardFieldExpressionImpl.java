package ru.dantalian.copvoc.persist.impl.query;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.query.TermCardFieldExpression;

public class TermCardFieldExpressionImpl implements TermCardFieldExpression {

	private final CardField field;

	private final String value;

	private final boolean wildcard;

	public TermCardFieldExpressionImpl(final CardField aField, final String aValue, final boolean aWildcard) {
		field = aField;
		value = aValue;
		wildcard = aWildcard;
	}

	@Override
	public CardField getField() {
		return field;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isWildcard() {
		return wildcard;
	}

}
