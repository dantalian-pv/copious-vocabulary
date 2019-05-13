package ru.dantalian.copvoc.persist.api.query;

import ru.dantalian.copvoc.persist.api.model.CardField;

public interface TermCardFieldExpression extends QueryExpression {

	CardField getField();

	String getValue();

	boolean isWildcard();

}
