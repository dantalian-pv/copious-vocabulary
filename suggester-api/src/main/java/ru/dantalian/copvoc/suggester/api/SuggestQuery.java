package ru.dantalian.copvoc.suggester.api;

import ru.dantalian.copvoc.suggester.api.model.Pair;

public interface SuggestQuery {

	SuggestQueryType getType();

	Pair<String, String> getWhere();

	Pair<String, String> getNot();

	int limit();

}
