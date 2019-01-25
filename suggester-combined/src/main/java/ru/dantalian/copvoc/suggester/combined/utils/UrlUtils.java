package ru.dantalian.copvoc.suggester.combined.utils;

import java.net.URI;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class UrlUtils {

	private UrlUtils() {
	}

	public static Map<String, List<String>> splitQuery(final URI aUri) {
		if (aUri.getQuery() == null || aUri.getQuery().isEmpty()) {
			return Collections.emptyMap();
		}
		return Arrays.stream(aUri.getQuery().split("&"))
				.map(UrlUtils::splitQueryParameter)
				.collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new,
						Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
	}

	public static SimpleImmutableEntry<String, String> splitQueryParameter(final String it) {
		final int idx = it.indexOf("=");
		final String key = idx > 0 ? it.substring(0, idx) : it;
		final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
		return new SimpleImmutableEntry<>(key, value);
	}

}
