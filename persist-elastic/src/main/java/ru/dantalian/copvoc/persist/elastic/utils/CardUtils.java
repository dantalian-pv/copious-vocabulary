package ru.dantalian.copvoc.persist.elastic.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public final class CardUtils {

	private CardUtils() {
	}

	public static String asPersistName(final CardField aCardField) {
		return aCardField.getName() + "_" + getIndexType(aCardField.getType());
	}

	public static String asPojoName(final String aName) {
		return aName.replaceAll("_\\w+$", "");
	}

	public static Map<String, CardField> asFieldsMap(final List<CardField> aFields) {
		final Map<String, CardField> fieldMap = new HashMap<>();
		for (final CardField field: aFields) {
			fieldMap.put(field.getName(), field);
		}
		return fieldMap;
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
