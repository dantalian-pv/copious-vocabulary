package ru.dantalian.copvoc.persist.api.query;

import ru.dantalian.copvoc.persist.api.model.CardField;

public interface ValueCardFieldExpression extends QueryExpression {

	CardField getField();

	Object getValue();

}
