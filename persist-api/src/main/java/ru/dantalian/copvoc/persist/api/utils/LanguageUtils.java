package ru.dantalian.copvoc.persist.api.utils;

import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.impl.model.PojoLanguage;

public final class LanguageUtils {

	private LanguageUtils() {
	}

	public static String asString(final Language aLanguage) {
		return aLanguage.getName() + "_" + aLanguage.getCountry()
			+ ((aLanguage.getVariant() == null || aLanguage.getVariant().isEmpty()) ? "" : "_" + aLanguage.getVariant());
	}

	public static Language asLanguage(final String aLanguage) {
		final String[] parts = aLanguage.split("_");
		if (parts.length < 2) {
			throw new IllegalArgumentException("Malformed language format in: " + aLanguage);
		}
		return new PojoLanguage(parts[0], parts[1], parts.length == 3 ? parts[2] : "", "");
	}

}
