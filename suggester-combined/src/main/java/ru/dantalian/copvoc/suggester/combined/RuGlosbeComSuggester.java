package ru.dantalian.copvoc.suggester.combined;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.Suggester;
import ru.dantalian.copvoc.suggester.api.model.Pair;
import ru.dantalian.copvoc.suggester.api.model.Suggest;
import ru.dantalian.copvoc.suggester.combined.model.PojoSuggest;

@Component("glosbe_s")
@Order(20)
public class RuGlosbeComSuggester implements Suggester {

	private static final Logger logger = LoggerFactory.getLogger(RuGlosbeComSuggester.class);

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public boolean accept(final Pair<String, String> aSourceTarget, final SuggestQueryType aType) {
		final boolean lang = (aSourceTarget.getKey().contains("ru") && aSourceTarget.getValue().contains("jp"))
				|| (aSourceTarget.getKey().contains("jp") && aSourceTarget.getValue().contains("ru"));
		return lang && (aType == SuggestQueryType.STRING || aType == SuggestQueryType.TEXT);
	}

	@Override
	public List<Suggest> suggest(final String aUser, final SuggestQuery aQuery) throws SuggestException {
		final String source = aQuery.getSourceTarget().getKey().contains("ru") ? "ru" : "ja";
		final String target = aQuery.getSourceTarget().getValue().contains("ru") ? "ru" : "ja";
		final String[] suggestKeys = restTemplate.getForObject(
				URI.create("https://ru.glosbe.com/ajax/phrasesAutosuggest?from=" + source + "&dest=" + target + "&phrase="
						+ aQuery.getWhere().getValue()),
				String[].class);
		final List<Suggest> suggests = new LinkedList<>();
		final int items = Math.min(suggestKeys.length, aQuery.limit());
		final String vocabularyId = "vocabulary_id".equals(aQuery.getNot().getKey()) ? aQuery.getNot().getValue() : null;
		for (int i = 0; i < items; i++) {
			final String suggestKey = suggestKeys[i];
			try {
			suggests.add(new PojoSuggest(URI.create("glosbe://glosbe?word=" + URLEncoder.encode(suggestKey, "UTF-8")
					+ "&vocabulary_id=" + vocabularyId + "&source=" + source + "&target=" + target),
					"ru.glosbe.com", aQuery.getWhere().getKey(), suggestKey, "", 1.0d));
			} catch (final UnsupportedEncodingException e) {
				logger.warn("Failed to suggest", e);
			}
		}
		return suggests;
	}

}
