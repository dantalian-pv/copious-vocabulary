package ru.dantalian.copvoc.suggester.api.query;

import ru.dantalian.copvoc.suggester.api.SuggestQueryBuilder;

public final class SuggestQueryFactory {

	private SuggestQueryFactory() {
	}

	public static SuggestQueryBuilder newBuilder() {
		return new SuggestQueryBuilderImpl();
	}

}
