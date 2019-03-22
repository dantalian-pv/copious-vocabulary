package ru.dantalian.copvoc.core.utils;

import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatType;
import ru.dantalian.copvoc.persist.impl.model.PojoCardStat;

public final class CardStatFactory {

	private CardStatFactory() {
	}

	public static <T> CardStat newStat(final String aName, final T aValue, final CardStatType aType) {
		return new PojoCardStat(aName, aValue, aType);
	}

}
