package ru.dantalian.copvoc.suggester.api.query;

import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.model.Pair;

public class SuggestQueryImpl implements SuggestQuery {

	private final SuggestQueryType type;

	private final Pair<String, String> where;

	private final Pair<String, String> not;

	private final int limit;

	public SuggestQueryImpl(final SuggestQueryType aType, final Pair<String, String> aWhere, final Pair<String, String> aNot, final int aLimit) {
		type = aType;
		where = aWhere;
		not = aNot;
		limit = aLimit;
	}

	@Override
	public SuggestQueryType getType() {
		return type;
	}

	@Override
	public Pair<String, String> getWhere() {
		return where;
	}

	@Override
	public Pair<String, String> getNot() {
		return not;
	}

	@Override
	public int limit() {
		return limit;
	}

	@Override
	public String toString() {
		return "SuggestQueryImpl [type=" + type + ", where=" + where + ", not=" + not + ", limit=" + limit + "]";
	}

}
