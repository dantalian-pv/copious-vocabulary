package ru.dantalian.copvoc.persist.api.query;

import ru.dantalian.copvoc.persist.api.Builder;

public interface BoolExpressionBuilder extends Builder<BoolExpression> {

	BoolExpressionBuilder must(QueryExpression aExpression);

	BoolExpressionBuilder should(QueryExpression aExpression);

	BoolExpressionBuilder not(QueryExpression aExpression);

}
