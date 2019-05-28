package ru.dantalian.copvoc.suggester.combined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.Suggester;
import ru.dantalian.copvoc.suggester.api.model.Pair;
import ru.dantalian.copvoc.suggester.api.model.Suggest;
import ru.dantalian.copvoc.suggester.combined.config.SuggesterSettings;

@Component("root")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CombinedSuggester implements Suggester {

	private static final Logger logger = LoggerFactory.getLogger(CombinedSuggester.class);

	@Autowired
	private List<Suggester> suggesters;

	@Autowired
	private SuggesterSettings settings;

	@Override
	public String getName() {
		return "root";
	}

	@Override
	public boolean accept(final Pair<String, String> aSourceTarget, final SuggestQueryType aType) {
		return true;
	}

	@Override
	public List<Suggest> suggest(final String aUser, final SuggestQuery aQuery) throws SuggestException {
		final List<Suggest> suggests = Collections.synchronizedList(new ArrayList<>());
		final List<CompletableFuture<List<Suggest>>> futures = new LinkedList<>();
		for (final Suggester suggester: suggesters) {
			if (suggester == this) {
				continue;
			}
			if (!settings.getEnabledSuggesters().contains(suggester.getName())) {
				continue;
			}
			if (!suggester.accept(aQuery.getSourceTarget(), aQuery.getType())) {
				continue;
			}
			final CompletableFuture<List<Suggest>> future = CompletableFuture.supplyAsync(
					() -> suggest(suggester, aUser, aQuery)
			);
			future.thenAcceptAsync(aList -> suggests.addAll(aList));
			futures.add(future);
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		Collections.sort(suggests);
		return suggests;
	}

	private List<Suggest> suggest(final Suggester aSuggester, final String aUser, final SuggestQuery aQuery) {
		try {
			return aSuggester.suggest(aUser, aQuery);
		} catch (final SuggestException e) {
			logger.error("Failed to call suggester {}", aSuggester, e);
		}
		return Collections.emptyList();
	}

}
