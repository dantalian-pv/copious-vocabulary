package ru.dantalian.copvoc.suggester.api;

public interface SuggestQueryBuilder extends Builder<SuggestQuery> {

	SuggestQueryBuilder asString();

	SuggestQueryBuilder asText();

	SuggestQueryBuilder setType(SuggestQueryType aType);

	SuggestQueryBuilder where(String aKey, String aValue);

	SuggestQueryBuilder not(String aKey, String aValue);

	SuggestQueryBuilder limit(int aLimit);

}
