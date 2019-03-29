package ru.dantalian.copvoc.web.utils;

import java.util.HashMap;
import java.util.Map;

import ru.dantalian.copvoc.core.stats.DefaultCardStats;
import ru.dantalian.copvoc.core.utils.CardStatFactory;
import ru.dantalian.copvoc.persist.api.model.CardStat;

public final class StatsUtils {

	private StatsUtils() {
	}

	public static Map<String, CardStat> defaultStats() {
		final Map<String, CardStat> stats = new HashMap<>();
		for (final DefaultCardStats defStat: DefaultCardStats.values()) {
			stats.put(defStat.getName(), CardStatFactory.newStat(defStat.getName(), null, defStat.getType()));
		}
		return stats;
	}

}
