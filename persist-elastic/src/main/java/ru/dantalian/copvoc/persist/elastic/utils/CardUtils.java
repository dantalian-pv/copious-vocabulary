package ru.dantalian.copvoc.persist.elastic.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatType;
import ru.dantalian.copvoc.persist.api.utils.Validator;
import ru.dantalian.copvoc.persist.impl.model.PojoCardStat;

public final class CardUtils {

	private CardUtils() {
	}

	public static String asPersistContentName(final CardField aCardField) {
		return "content." + asPersistName(aCardField);
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

	public static Map<String, CardStat> asCardStats(final Map<String, ?> stats) {
		if (stats == null) {
			return Collections.emptyMap();
		}
		final Map<String, CardStat> statsMap = new HashMap<>();
		for (final Entry<String, ?> stat: stats.entrySet()) {
			final String statName = CardUtils.asPojoName(stat.getKey());
			final CardStatType type = CardStatType.valueOf(CardUtils.getSuffix(stat.getKey()).toUpperCase());
			statsMap.put(statName, new PojoCardStat(statName, stat.getValue(), type));
		}
		return statsMap;
	}

	public static Map<String, Object> asPersistStats(final Map<String, CardStat> aStatsMap) {
		final Map<String, Object> stats = new HashMap<>();
		for (final Entry<String, CardStat> stat: aStatsMap.entrySet()) {
			final CardStat cardStat = stat.getValue();
			Validator.checkNotNull(cardStat.getType(), "No type for stat " + cardStat.getName());
			stats.put(CardUtils.asPersistStatName(cardStat), cardStat.getValue());
		}
		return stats;
	}

}
