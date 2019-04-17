package ru.dantalian.copvoc.suggester.combined;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

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

@Component("yarksi_s")
@Order(30)
public class YarksiRuSuggester implements Suggester {

	private static final Logger logger = LoggerFactory.getLogger(YarksiRuSuggester.class);

	private static final String SUGGESTS_KEYWORD = "suggests_keyword";

	private static final String QUERY_KEYWORD = "query_keyword";

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
			if (!"word".equals(aQuery.getWhere().getKey())) {
				return Collections.emptyList();
			}
			final String source = aQuery.getSourceTarget().getKey().contains("ru") ? "ru" : "ja";
			final String target = aQuery.getSourceTarget().getValue().contains("ru") ? "ru" : "ja";
			final String word = aQuery.getWhere().getValue();
			final List<String> suggestKeys;

			final String cacheId = serialize(aQuery);
			final String cacheHash = CommonUtils.hash(cacheId);
			Map<String, Object> map = cache.load(cacheHash);
			if (map == null) {
				// Cache miss
				final URI url = URI.create("https://yarxi.ru/search.php");

				final Map<String, String> data = new HashMap<>();
				data.put("K", "");
				data.put("R", "");
				data.put("M", word);
				data.put("D", "0");
				data.put("NS", "0");
				data.put("F", "0");

				final Document doc = Jsoup.connect(url.toString())
						.data(data)
						.post();
				suggestKeys = doc.select(".mmno ~ td").eachText();
				// Also save in cache
				map = new HashMap<>();
				map.put(PersistCacheManager.ID, cacheHash);
				map.put(QUERY_KEYWORD, cacheId);
				map.put(SUGGESTS_KEYWORD, suggestKeys);
				cache.save(map);

				saveInCacheForRetrieval(doc, word);
			} else {
				suggestKeys = ((List<String>) map.get(SUGGESTS_KEYWORD));
			}

			final List<Suggest> suggests = new LinkedList<>();
			final int items = Math.min(suggestKeys.size(), aQuery.limit());
			final String vocabularyId = "vocabulary_id".equals(aQuery.getNot().getKey()) ? aQuery.getNot().getValue() : null;
			for (int i = 0; i < items; i++) {
				final String suggestKey = suggestKeys.get(i);
				try {
				suggests.add(new PojoSuggest(URI.create("yarxi://yarxi?word=" + URLEncoder.encode(word, "UTF-8")
						+ "&vocabulary_id=" + vocabularyId + "&source=" + source + "&target=" + target),
						"yarxi.ru", aQuery.getWhere().getKey(), suggestKey, "", 1.0d));
				} catch (final UnsupportedEncodingException e) {
					logger.warn("Failed to suggest", e);
				}
			}
			return suggests;
		} catch (PersistException | RestClientException | IOException e) {
			throw new SuggestException("Failed to retrieve suggests", e);
		}
	}

	private void saveInCacheForRetrieval(final Document aDoc, final String aWord)
			throws UnsupportedEncodingException, PersistException {
		final Element answer = aDoc.select(".kunj")
				.first();
		final Element kun = aDoc.select(".kunreading")
				.first();
		final Element on = aDoc.select(".kunreading_ch")
				.first();

		final String url = "https://yarxi.ru/search.php?K=&R=&S=&D=0&NS=0&F=0&M="
				+ URLEncoder.encode(aWord, "UTF-8");
		final String urlHash = CommonUtils.hash(url);

		Map<String, Object> cacheMap = new HashMap<>();
		final Map<String, Object> mapForCache = new HashMap<>();

		mapForCache.put("translation_keyword", answer.text());
		mapForCache.put("word_keyword", aWord);
		mapForCache.put("example_text", "");

		mapForCache.put("kunyomi_keyword", kun.text());

		mapForCache.put("onyomi_keyword", on.text());

		cacheMap = new HashMap<>();
		cacheMap.put(PersistCacheManager.ID, urlHash);
		cacheMap.put("url_keyword", url);
		cacheMap.put("map", mapForCache);

		cache.save(cacheMap);
	}

	private String serialize(final SuggestQuery aQuery) {
		final String source = aQuery.getSourceTarget().getKey().contains("ru") ? "ru" : "ja";
		final String target = aQuery.getSourceTarget().getValue().contains("ru") ? "ru" : "ja";
		final String phrase = aQuery.getWhere().getValue();
		return "YarksiRu_suggest_" + source + "_" + target + "_" + phrase;
	}

}
