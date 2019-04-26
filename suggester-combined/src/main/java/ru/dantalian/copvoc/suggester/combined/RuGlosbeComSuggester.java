package ru.dantalian.copvoc.suggester.combined;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ru.dantalian.copvoc.persist.api.PersistCacheManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.utils.CommonUtils;
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

	private static final String SUGGESTS_KEYWORD = "suggests_keyword";

	private static final String QUERY_KEYWORD = "query_keyword";

	@Override
	public String getName() {
		return "glosbe";
	}

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PersistCacheManager cache;

	@Override
	public boolean accept(final Pair<String, String> aSourceTarget, final SuggestQueryType aType) {
		final boolean lang = (aSourceTarget.getKey().contains("ru") && aSourceTarget.getValue().contains("jp"))
				|| (aSourceTarget.getKey().contains("jp") && aSourceTarget.getValue().contains("ru"));
		return lang && (aType == SuggestQueryType.STRING || aType == SuggestQueryType.TEXT);
	}

	@Override
	public List<Suggest> suggest(final String aUser, final SuggestQuery aQuery) throws SuggestException {
		try {
			final String source = aQuery.getSourceTarget().getKey().contains("ru") ? "ru" : "ja";
			final String target = aQuery.getSourceTarget().getValue().contains("ru") ? "ru" : "ja";
			final String[] suggestKeys;

			final String cacheId = serialize(aQuery);
			final String cacheHash = CommonUtils.hash(cacheId);
			Map<String, Object> map = cache.load(cacheHash);
			if (map == null) {
				// Cache miss
				suggestKeys = restTemplate.getForObject(
						URI.create("https://ru.glosbe.com/ajax/phrasesAutosuggest?from=" + source + "&dest=" + target + "&phrase="
								+ URLEncoder.encode(aQuery.getWhere().getValue(), "UTF-8")),
						String[].class);
				// Also save in cache
				map = new HashMap<>();
				map.put(PersistCacheManager.ID, cacheHash);
				map.put(QUERY_KEYWORD, cacheId);
				map.put(SUGGESTS_KEYWORD, Arrays.asList(suggestKeys));
				cache.save(map);
			} else {
				suggestKeys = ((List<String>) map.get(SUGGESTS_KEYWORD)).toArray(new String[0]);
			}

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
		} catch (PersistException | RestClientException | UnsupportedEncodingException e1) {
			throw new SuggestException("Failed to retrieve suggests", e1);
		}
	}

	private String serialize(final SuggestQuery aQuery) {
		final String source = aQuery.getSourceTarget().getKey().contains("ru") ? "ru" : "ja";
		final String target = aQuery.getSourceTarget().getValue().contains("ru") ? "ru" : "ja";
		final String phrase = aQuery.getWhere().getValue();
		return "RuGlosbe_suggest_" + source + "_" + target + "_" + phrase;
	}

}
