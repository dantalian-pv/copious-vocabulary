package ru.dantalian.copvoc.suggester.api.query;

import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;

public class SuggestQueryImpl implements SuggestQuery {

	private final SuggestQueryType type;

	private final String key;

	private final String value;

	private final int limit;

	public SuggestQueryImpl(final SuggestQueryType aType, final String aKey, final String aValue, final int aLimit) {
		type = aType;
		key = aKey;
		value = aValue;
		limit = aLimit;
	}

	@Override
	public SuggestQueryType getType() {
		return type;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public int limit() {
		return limit;
	}

	@Override
	public String toString() {
		return "SuggestQueryImpl [type=" + type + ", key=" + key + ", value=" + value + ", limit=" + limit + "]";
	}

}
