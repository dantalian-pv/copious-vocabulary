package ru.dantalian.copvoc.persist.api.query;

public interface ValueExpression extends QueryExpression {

	String getName();

	Object getValue();

}
