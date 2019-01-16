package ru.dantalian.copvoc.persist.api.query;

import ru.dantalian.copvoc.persist.api.Builder;

public interface BoolExpressionBuilder extends Builder<BoolExpression> {

	BoolExpressionBuilder must(CardsExpression aExpression);

	BoolExpressionBuilder should(CardsExpression aExpression);

	BoolExpressionBuilder not(CardsExpression aExpression);

}
