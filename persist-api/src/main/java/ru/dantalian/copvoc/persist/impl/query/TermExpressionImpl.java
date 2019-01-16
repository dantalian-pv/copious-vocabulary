package ru.dantalian.copvoc.persist.impl.query;

import ru.dantalian.copvoc.persist.api.query.TermExpression;

public class TermExpressionImpl implements TermExpression {

	private final String name;

	private final String value;

	private final boolean wildcard;

	public TermExpressionImpl(final String aName, final String aValue, final boolean aWildcard) {
		name = aName;
		value = aValue;
		wildcard = aWildcard;
	}

	@Override
	public String getName() {
		return name;
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
