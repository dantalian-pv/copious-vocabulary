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
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.utils.CommonUtils;
import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.UniversalRetrieval;
import ru.dantalian.copvoc.suggester.combined.utils.UrlUtils;

@Component("yarksi_r")
@Order(30)
public class YarksiRuRetrieval implements UniversalRetrieval {

	@Autowired
	private PersistCacheManager cache;

	@Override
	public boolean accept(final URI aSource) {
		final String scheme = aSource.getScheme();
		return "yarxi".equals(scheme);
	}

	@Override
	public Map<String, Object> retrieve(final String aUser, final URI aSource) throws SuggestException {
		try {
			final Map<String, List<String>> splitQuery = UrlUtils.splitQuery(aSource);
			final String word = URLDecoder.decode(splitQuery.get("word").get(0), "UTF-8");
			final String source = splitQuery.get("source").get(0);
			final String target = splitQuery.get("target").get(0);

			final String url = "https://yarxi.ru/search.php?K=&R=&S=&D=0&NS=0&F=0&M="
					+ URLEncoder.encode(word, "UTF-8");
			final String urlHash = CommonUtils.hash(url);

			final Map<String, Object> map;
			Map<String, Object> cacheMap = cache.load(urlHash);
			if (cacheMap == null) {
				map = new HashMap<>();
				final Map<String, Object> mapForCache = new HashMap<>();
				final Map<String, String> data = new HashMap<>();
				data.put("K", "");
				data.put("R", "");
				data.put("M", word);
				data.put("D", "0");
				data.put("NS", "0");
				data.put("F", "0");

				final Document doc = Jsoup.connect("https://yarxi.ru/search.php")
						.data(data)
						.post();

				final Element answer = doc.select(".kunj")
						.first();
				final Element kun = doc.select(".kunreading")
						.first();
				final Element on = doc.select(".kunreading_ch")
						.first();

				map.put("translation", answer.text());
				mapForCache.put("translation_keyword", answer.text());
				map.put("word", word);
				mapForCache.put("word_keyword", word);
				map.put("example", "");
				mapForCache.put("example_text", "");

				map.put("kunyomi", kun);
				mapForCache.put("kunyomi_keyword", kun);

				map.put("onyomi", on);
				mapForCache.put("onyomi_keyword", on);

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
