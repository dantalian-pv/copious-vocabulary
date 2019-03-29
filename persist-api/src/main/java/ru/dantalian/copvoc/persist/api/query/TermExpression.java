package ru.dantalian.copvoc.persist.api.query;

public interface TermExpression extends QueryExpression {

	String getName();

	String getValue();

	boolean isWildcard();

}
