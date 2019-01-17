package ru.dantalian.copvoc.suggester.combined;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.Suggester;
import ru.dantalian.copvoc.suggester.api.model.Suggest;

@Component("root")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CombinedSuggester implements Suggester {

	@Autowired
	private List<Suggester> suggesters;

	@Override
	public boolean accept(final SuggestQueryType aType) {
		return true;
	}

	@Override
	public List<Suggest> suggest(final String aUser, final SuggestQuery aQuery) throws SuggestException {
		final List<Suggest> suggests = new LinkedList<>();
		for (final Suggester suggester: suggesters) {
			if (suggester == this) {
				continue;
			}
			if (!suggester.accept(aQuery.getType())) {
				continue;
			}
			suggests.addAll(suggester.suggest(aUser, aQuery));
		}
		Collections.sort(suggests);
		return suggests;
	}

}
