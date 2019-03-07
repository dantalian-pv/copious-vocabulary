package ru.dantalian.copvoc.persist.api.utils;

public final class CommonUtils {

	private CommonUtils() {
	}

	public static String hash(final String aSirialized) {
		return Integer.toHexString(aSirialized.hashCode());
	}

}
