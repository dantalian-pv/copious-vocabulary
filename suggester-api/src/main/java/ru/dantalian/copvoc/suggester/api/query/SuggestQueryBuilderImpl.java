package ru.dantalian.copvoc.suggester.api.query;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryBuilder;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;

public class SuggestQueryBuilderImpl implements SuggestQueryBuilder {

	private SuggestQueryType type;

	private String key;

	private String value;

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
	public SuggestQueryBuilder with(final String aKey, final String aValue) {
		key = aKey;
		value = aValue;
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
		if (key == null || value == null || key.isEmpty() || value.isEmpty()) {
			throw new IllegalArgumentException("key and value should be defined");
		}
		return new SuggestQueryImpl(type, key, value, limit);
	}

}
