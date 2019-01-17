package ru.dantalian.copvoc.suggester.api;

import java.util.List;

import ru.dantalian.copvoc.suggester.api.model.Suggest;

public interface Suggester {

	boolean accept(SuggestQueryType aType);

	List<Suggest> suggest(String aUser, SuggestQuery aQuery) throws SuggestException;

}
