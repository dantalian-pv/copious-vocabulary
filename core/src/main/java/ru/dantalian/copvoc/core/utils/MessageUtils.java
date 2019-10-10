package ru.dantalian.copvoc.core.utils;

import org.slf4j.helpers.MessageFormatter;

public final class MessageUtils {

	private MessageUtils() {
	}

	public static String message(final String messagePattern, final Object... args) {
		return MessageFormatter.arrayFormat(messagePattern, args).getMessage();
	}

}
