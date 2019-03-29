package ru.dantalian.copvoc.persist.impl.query.sort;

import java.util.Map;

import ru.dantalian.copvoc.persist.api.query.sort.ScriptSortExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortOrder;
import ru.dantalian.copvoc.persist.api.query.sort.SortType;

public class ScriptSortExpressionImpl implements ScriptSortExpression {

	private final SortType type;

	private final SortOrder order;

	private final String script;

	private final Map<String, Object> params;

	public ScriptSortExpressionImpl(final SortType aType, final SortOrder aOrder, final String aScript,
			final Map<String, Object> aParams) {
		type = aType;
		order = aOrder;
		script = aScript;
		params = aParams;
	}

	@Override
	public SortType getType() {
		return type;
	}

	@Override
	public SortOrder getOrder() {
		return order;
	}

	@Override
	public String getScript() {
		return script;
	}

	@Override
	public Map<String, Object> getParams() {
		return params;
	}

}
