package ru.dantalian.copvoc.suggester.api;

public interface SuggestQuery {

	SuggestQueryType getType();

	String getKey();

	String getValue();

	int limit();

}
