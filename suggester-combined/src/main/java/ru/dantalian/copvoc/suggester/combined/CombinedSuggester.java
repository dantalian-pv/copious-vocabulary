package ru.dantalian.copvoc.suggester.combined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

	@Autowired
	private ExecutorService pool;

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
		try {
			final List<Suggest> suggests = new ArrayList<>();
			final List<Callable<List<Suggest>>> commands = new LinkedList<>();
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
				commands.add(new SuggestCommand(suggester, aUser, aQuery));
			}
			final List<Future<List<Suggest>>> futures = pool.invokeAll(commands);
			for (final Future<List<Suggest>> future: futures) {
				try {
					suggests.addAll(future.get(10, TimeUnit.SECONDS));
				} catch (final ExecutionException | TimeoutException e) {
					logger.error("Failed to get suggest", e);
				}
			}
			Collections.sort(suggests);
			return suggests;
		} catch (final InterruptedException e) {
			throw new SuggestException("Failed to get suggests", e);
		}
	}

	private class SuggestCommand implements Callable<List<Suggest>> {

		private final Suggester suggester;
		private final String user;
		private final SuggestQuery query;

		public SuggestCommand(final Suggester aSuggester, final String aUser, final SuggestQuery aQuery) {
			suggester = aSuggester;
			user = aUser;
			query = aQuery;
		}

		@Override
		public List<Suggest> call() throws Exception {
			return suggester.suggest(user, query);
		}

	}

}
