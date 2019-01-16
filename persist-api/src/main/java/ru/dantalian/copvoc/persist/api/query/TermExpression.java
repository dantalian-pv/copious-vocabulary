package ru.dantalian.copvoc.persist.api.query;

public interface TermExpression extends CardsExpression {

	String getName();

	String getValue();

	boolean isWildcard();

}
