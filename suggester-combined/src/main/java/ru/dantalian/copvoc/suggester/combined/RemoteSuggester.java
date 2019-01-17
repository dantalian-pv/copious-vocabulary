package ru.dantalian.copvoc.suggester.combined;

import java.util.Collections;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.Suggester;
import ru.dantalian.copvoc.suggester.api.model.Suggest;

@Component("remote")
@Order(20)
public class RemoteSuggester implements Suggester {

	@Override
	public boolean accept(final SuggestQueryType aType) {
		return aType == SuggestQueryType.STRING || aType == SuggestQueryType.TEXT;
	}

	@Override
	public List<Suggest> suggest(final String aUser, final SuggestQuery aQuery) throws SuggestException {
		return Collections.emptyList();
	}

}
