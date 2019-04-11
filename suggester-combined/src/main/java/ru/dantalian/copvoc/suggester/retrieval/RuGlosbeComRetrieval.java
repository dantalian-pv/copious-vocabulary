package ru.dantalian.copvoc.suggester.retrieval;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.persist.api.PersistCacheManager;
import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.utils.CommonUtils;
import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.UniversalRetrieval;
import ru.dantalian.copvoc.suggester.combined.utils.UrlUtils;

@Component("glosbe_r")
@Order(20)
public class RuGlosbeComRetrieval implements UniversalRetrieval {

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private PersistCacheManager cache;

	@Override
	public boolean accept(final URI aSource) {
		final String scheme = aSource.getScheme();
		return "glosbe".equals(scheme);
	}

	@Override
	public Map<String, Object> retrieve(final String aUser, final URI aSource) throws SuggestException {
		try {
			final Map<String, List<String>> splitQuery = UrlUtils.splitQuery(aSource);
			final String word = URLDecoder.decode(splitQuery.get("word").get(0), "UTF-8");
			final String source = splitQuery.get("source").get(0);
			final String target = splitQuery.get("target").get(0);

			final String url = "https://ru.glosbe.com/"
					+ source + "/" + target + "/" +  URLEncoder.encode(word, "UTF-8").replace("+", "%20");
			final String urlHash = CommonUtils.hash(url);

			final Map<String, Object> map;
			Map<String, Object> cacheMap = cache.load(urlHash);
			if (cacheMap == null) {
				map = new HashMap<>();
				final Map<String, Object> mapForCache = new HashMap<>();
				final Document doc = Jsoup.connect(url).get();

				Element ja = doc.select("#phraseTranslation > div > ul > li:nth-child(1) > div.examples > div > div:nth-child(2) > div:nth-child(2)").first();
				if (ja == null) {
					ja = doc.select("#tmTable > div:nth-child(1) > div:nth-child(2) > span > span > span").first();
				}
				final Element answer = doc.select("#phraseTranslation > div > ul > li:nth-child(1) > div.text-info > strong")
						.first();

				map.put("translation", answer.text());
				mapForCache.put("translation_keyword", answer.text());
				map.put("word", word);
				mapForCache.put("word_keyword", word);
				final String text = ja == null ? "" : ja.text().replaceAll("(" + answer.text() + ")", "[$1]");
				map.put("example", text);
				mapForCache.put("example_text", text);

				cacheMap = new HashMap<>();
				cacheMap.put(PersistCacheManager.ID, urlHash);
				cacheMap.put("url_keyword", url);
				cacheMap.put("map", mapForCache);

				cache.save(cacheMap);
			} else {
				map = ((Map<String, Object>) cacheMap.get("map"))
						.entrySet()
						.stream()
						.collect(Collectors.toMap(aItem -> aItem.getKey().replaceAll("(.*)_\\w++$","$1"),
								aItem -> aItem.getValue()));
			}
			return map;
		} catch (final IOException | PersistException e) {
			throw new SuggestException("Failed to get resource " + aSource, e);
		}
	}

}
