package ru.dantalian.copvoc.suggester.api;

import java.util.List;

import ru.dantalian.copvoc.suggester.api.model.Pair;
import ru.dantalian.copvoc.suggester.api.model.Suggest;

public interface Suggester {

	String getName();

	boolean accept(Pair<String, String> aSourceTarget, SuggestQueryType aType);

	List<Suggest> suggest(String aUser, SuggestQuery aQuery) throws SuggestException;

}
