package ru.dantalian.copvoc.suggester.api;

import java.util.List;

import ru.dantalian.copvoc.suggester.api.model.Suggest;

public interface Suggester {

	List<Suggest> suggest(String aUser, SuggestQuery aQuery) throws SuggestException;

}
