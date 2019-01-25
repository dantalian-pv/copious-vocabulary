package ru.dantalian.copvoc.suggester.api;

public interface SuggestQueryBuilder extends Builder<SuggestQuery> {

	SuggestQueryBuilder asString();

	SuggestQueryBuilder asText();

	SuggestQueryBuilder setType(SuggestQueryType aType);

	SuggestQueryBuilder where(String aKey, String aValue);

	SuggestQueryBuilder not(String aKey, String aValue);

	SuggestQueryBuilder setSourceTarget(String aSource, String aTarget);

	SuggestQueryBuilder limit(int aLimit);

}
