package ru.dantalian.copvoc.persist.api.query;

import java.util.List;

public interface BoolExpression extends CardsExpression {

	List<CardsExpression> must();

	List<CardsExpression> not();

	List<CardsExpression> should();



}
