package ru.dantalian.copvoc.persist.elastic.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatType;

public final class CardUtils {

	private CardUtils() {
	}

	public static String asPersistName(final CardField aCardField) {
		return aCardField.getName() + "_" + getIndexType(aCardField.getType());
	}

	public static String asPersistStatName(final CardStat aStat) {
		return aStat.getName() + "_" + getIndexSuffix(aStat.getType());
	}

	public static String asPojoName(final String aName) {
		return aName.replaceAll("_\\w+$", "");
	}

	public static String getSuffix(final String aName) {
		final int indexOf = aName.lastIndexOf("_");
		if (indexOf == -1) {
			throw new IllegalArgumentException("No suffix found in " + aName);
		}
		return aName.substring(indexOf + 1);
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

	private static String getIndexSuffix(final CardStatType aType) {
		switch (aType) {
			case DATE:
				return "date";
			case DOUBLE:
				return "double";
			case LONG:
				return "long";
			default:
				return "long";
		}
	}

}
