package ru.dantalian.copvoc.persist.impl.query;

import java.util.Collections;
import java.util.List;

import ru.dantalian.copvoc.persist.api.query.TermsExpression;

public class TermsExpressionImpl<T> implements TermsExpression<T> {

	private final String name;

	private final List<T> values;

	public TermsExpressionImpl(final String aName, final List<T> aValues) {
		name = aName;
		values = Collections.unmodifiableList(aValues);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<T> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "TermsExpressionImpl [name=" + name + ", values=" + values + "]";
	}

}
