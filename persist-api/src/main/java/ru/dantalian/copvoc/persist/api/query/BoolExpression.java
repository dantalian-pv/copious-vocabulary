package ru.dantalian.copvoc.persist.api.query;

import java.util.List;

public interface BoolExpression extends QueryExpression {

	List<QueryExpression> must();

	List<QueryExpression> not();

	List<QueryExpression> should();



}
