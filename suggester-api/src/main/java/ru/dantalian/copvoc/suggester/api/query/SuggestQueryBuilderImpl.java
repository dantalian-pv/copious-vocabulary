package ru.dantalian.copvoc.suggester.api.query;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryBuilder;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.model.Pair;

public class SuggestQueryBuilderImpl implements SuggestQueryBuilder {

	private SuggestQueryType type;

	private Pair<String, String> where;

	private Pair<String, String> not;

	private int limit = 10;

	@Override
	public SuggestQueryBuilder asString() {
		type = SuggestQueryType.STRING;
		return this;
	}

	@Override
	public SuggestQueryBuilder asText() {
		type = SuggestQueryType.TEXT;
		return this;
	}

	@Override
	public SuggestQueryBuilder setType(final SuggestQueryType aType) {
		type = aType;
		return this;
	}

	@Override
	public SuggestQueryBuilder where(final String aKey, final String aValue) {
		where = Pair.of(aKey, aValue);
		return this;
	}

	@Override
	public SuggestQueryBuilder not(final String aKey, final String aValue) {
		not = Pair.of(aKey, aValue);
		return this;
	}

	@Override
	public SuggestQueryBuilder limit(final int aLimit) {
		if (aLimit < 1) {
			throw new IllegalArgumentException("limit cannot be less than 1");
		}
		limit = aLimit;
		return this;
	}

	@Override
	public SuggestQuery build() throws SuggestException {
		if (where == null || where.getValue() == null || where.getValue().isEmpty()) {
			throw new IllegalArgumentException("value should be defined");
		}
		if (type == null) {
			type = SuggestQueryType.STRING;
		}
		return new SuggestQueryImpl(type, where, not, limit);
	}

}
