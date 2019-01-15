package ru.dantalian.copvoc.web.common;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public final class CardUtils {

	private CardUtils() {
	}

	public static String asPersistName(final CardField aCardField) {
		return aCardField.getName() + "_" + getIndexType(aCardField.getType());
	}

	public static String getIndexType(final CardFiledType aType) {
		switch (aType) {
			case MARKUP:
			case TEXT:
				return "text";
			default:
				return "keyword";
		}
	}

}
