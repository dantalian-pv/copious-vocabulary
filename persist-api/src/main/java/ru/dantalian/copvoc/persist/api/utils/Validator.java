package ru.dantalian.copvoc.persist.api.utils;

import java.util.Collection;

public final class Validator {

	private Validator() {
	}

	public static <T> T checkNotNull(final T aReference) {
		return checkNotNull(aReference, null);
	}

	public static <T> T checkNotNull(final T aReference, final String aMessage) {
		if (aReference == null) {
			throw new NullPointerException(aMessage);
		}
		return aReference;
	}

	public static String checkEmptyString(final String aString) {
		return checkEmptyString(aString, null);
	}

	public static String checkEmptyString(final String aString, final String aMessage) {
		if (aString == null) {
			throw new NullPointerException(aMessage);
		}
		if (aString.isEmpty()) {
			throw new IllegalArgumentException(aMessage);
		}
		return aString;
	}

	public static void checkCondition(final boolean aCondition, final String aMessage) {
		if (!aCondition) {
			throw new IllegalArgumentException(aMessage);
		}
	}

	public static <T extends Collection<?>> T checkEmptyCollection(final T aCollection) {
		return checkEmptyCollection(aCollection, null);
	}

	public static <T extends Collection<?>> T checkEmptyCollection(final T aCollection, final String aMessage) {
		checkNotNull(aCollection);
		if (aCollection.isEmpty()) {
			throw new IllegalStateException(aMessage);
		}
		return aCollection;
	}

}
