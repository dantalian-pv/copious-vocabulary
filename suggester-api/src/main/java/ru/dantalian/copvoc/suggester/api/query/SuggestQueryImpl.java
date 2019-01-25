package ru.dantalian.copvoc.suggester.api.query;

import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.model.Pair;

public class SuggestQueryImpl implements SuggestQuery {

	private final SuggestQueryType type;

	private final Pair<String, String> where;

	private final Pair<String, String> not;

	private final Pair<String, String> sourceTarget;

	private final int limit;

	public SuggestQueryImpl(final SuggestQueryType aType,
			final Pair<String, String> aWhere,
			final Pair<String, String> aNot,
			final Pair<String, String> aSourceTarget,
			final int aLimit) {
		type = aType;
		where = aWhere;
		not = aNot;
		sourceTarget = aSourceTarget;
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
	public Pair<String, String> getSourceTarget() {
		return sourceTarget;
	}

	@Override
	public int limit() {
		return limit;
	}

	@Override
	public String toString() {
		return "SuggestQueryImpl [type=" + type + ", where=" + where + ", not=" + not
				+ ", sourceTarget=" + sourceTarget + ", limit=" + limit + "]";
	}

}
