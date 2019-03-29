package ru.dantalian.copvoc.persist.api.query.sort;

public interface SortExpression {

	SortType getType();

	SortOrder getOrder();

}
