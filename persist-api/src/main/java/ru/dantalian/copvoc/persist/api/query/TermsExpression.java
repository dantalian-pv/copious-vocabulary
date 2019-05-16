package ru.dantalian.copvoc.persist.api.query;

import java.util.List;

public interface TermsExpression<T> extends QueryExpression {

	String getName();

	List<T> getValues();

}
