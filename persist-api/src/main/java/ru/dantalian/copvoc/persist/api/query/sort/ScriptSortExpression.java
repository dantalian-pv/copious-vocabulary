package ru.dantalian.copvoc.persist.api.query.sort;

import java.util.Map;

public interface ScriptSortExpression extends SortExpression {

	String getScript();

	Map<String, Object> getParams();

}
