package ru.dantalian.copvoc.core.utils;

import java.util.Date;

import ru.dantalian.copvoc.core.stats.DefaultCardStats;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.api.model.CardStatType;
import ru.dantalian.copvoc.persist.api.stats.StatAction;
import ru.dantalian.copvoc.persist.impl.model.PojoCardStat;
import ru.dantalian.copvoc.persist.impl.query.PojoCardStatAction;

public final class CardStatFactory {

	private CardStatFactory() {
	}

	public static <T> CardStat newStat(final String aName, final T aValue, final CardStatType aType) {
		return new PojoCardStat(aName, aValue, aType);
	}

	public static CardStatAction newAction(final String aName, final Object aValue, final CardStatType aType,
			final StatAction aAction) {
		return new PojoCardStatAction(aName, aValue, aType, aAction);
	}

	public static CardStatAction newAction(final DefaultCardStats aStat, final Object aValue,
			final StatAction aAction) {
		return newAction(aStat.getName(), aValue, aStat.getType(), aAction);
	}

	public static CardStatAction newIncAction(final DefaultCardStats aStat, final Object aValue) {
		return newAction(aStat, aValue, StatAction.ICREMENT);
	}

	public static CardStatAction newDecAction(final DefaultCardStats aStat, final Object aValue) {
		return newAction(aStat, aValue, StatAction.DECREMENT);
	}

	public static CardStatAction newSetAction(final DefaultCardStats aStat, final Object aValue) {
		return newAction(aStat, aValue, StatAction.SET);
	}

	public static CardStatAction newSuccessInc() {
		return newIncAction(DefaultCardStats.SUCESS, 1L);
	}

	public static CardStatAction newFailInc() {
		return newIncAction(DefaultCardStats.FAIL, 1L);
	}

	public static CardStatAction newSkipInc() {
		return newIncAction(DefaultCardStats.SKIP, 1L);
	}

	public static CardStatAction newVisitsInc() {
		return newIncAction(DefaultCardStats.VISITS, 1L);
	}

	public static CardStatAction newLastVisit() {
		return newSetAction(DefaultCardStats.LAST_VISIT, new Date());
	}

	public static CardStatAction newShardInc() {
		return newIncAction(DefaultCardStats.SHARED, 1L);
	}

	public static CardStatAction newShardDec() {
		return newDecAction(DefaultCardStats.SHARED, 1L);
	}

}
