package ru.dantalian.copvoc.suggester.api;

public interface SuggestQueryBuilder extends Builder<SuggestQuery> {

	SuggestQueryBuilder asString();

	SuggestQueryBuilder asText();

	SuggestQueryBuilder with(String aKey, String aValue);

	SuggestQueryBuilder limit(int aLimit);

}
