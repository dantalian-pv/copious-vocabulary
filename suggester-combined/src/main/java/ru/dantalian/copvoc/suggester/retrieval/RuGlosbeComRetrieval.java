package ru.dantalian.copvoc.suggester.retrieval;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import ru.dantalian.copvoc.persist.api.model.CardField;
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
		final Map<String, List<String>> splitQuery = UrlUtils.splitQuery(aSource);
		final String word = splitQuery.get("word").get(0);
		final UUID vocabularyId = UUID.fromString(splitQuery.get("vocabulary_id").get(0));
		final String source = splitQuery.get("source").get(0);
		final String target = splitQuery.get("target").get(0);

		try {
			final List<CardField> fields = fieldManager.listFields(aUser, vocabularyId);

			final String url = "https://ru.glosbe.com/"
					+ source + "/" + target + "/" + word;
			final String urlHash = CommonUtils.hash(url);

			final Map<String, Object> map;
			Map<String, Object> cacheMap = cache.load(urlHash);
			if (cacheMap == null) {
				map = new HashMap<>();
				final Map<String, Object> mapForCache = new HashMap<>();
				final Document doc = Jsoup.connect(url).get();

				final Element ja = doc.select("#phraseTranslation > div > ul > li:nth-child(1) > div.examples > div > div:nth-child(2) > div:nth-child(2)").first();
				final Element answer = doc.select("#phraseTranslation > div > ul > li:nth-child(1) > div.text-info > strong")
						.first();

				for (final CardField field: fields) {
					switch (field.getType()) {
						case ANSWER:
							map.put(field.getName(), answer.text());
							mapForCache.put(field.getName() + "_keyword", answer.text());
							break;
						case STRING:
							map.put(field.getName(), word);
							mapForCache.put(field.getName() + "_keyword", word);
							break;
						case MARKUP:
						case TEXT:
							final String text = ja.text().replaceAll("(" + answer.text() + ")", "[$1]");
							map.put(field.getName(), text);
							mapForCache.put(field.getName() + "_text", text);
							break;
						default:
							throw new IllegalArgumentException("Unknown field type");
					}
				}
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
