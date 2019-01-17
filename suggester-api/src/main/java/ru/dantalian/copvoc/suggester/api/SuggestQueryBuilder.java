package ru.dantalian.copvoc.suggester.api;

public interface SuggestQueryBuilder extends Builder<SuggestQuery> {

	SuggestQueryBuilder asString();

	SuggestQueryBuilder asText();

	SuggestQueryBuilder setType(SuggestQueryType aType);

	SuggestQueryBuilder with(String aKey, String aValue);

	SuggestQueryBuilder limit(int aLimit);

}
