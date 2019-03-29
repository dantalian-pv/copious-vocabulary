package ru.dantalian.copvoc.persist.impl.query.sort;

import ru.dantalian.copvoc.persist.api.query.sort.FieldSortExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortOrder;
import ru.dantalian.copvoc.persist.api.query.sort.SortType;

public class FieldSortExpressionImpl implements FieldSortExpression {

	private final SortType type;

	private final SortOrder order;

	private final String field;

	public FieldSortExpressionImpl(final SortType aType, final SortOrder aOrder, final String aField) {
		type = aType;
		order = aOrder;
		field = aField;
	}

	@Override
	public SortType getType() {
		return type;
	}

	@Override
	public SortOrder getOrder() {
		return order;
	}

	@Override
	public String getField() {
		return field;
	}

}
