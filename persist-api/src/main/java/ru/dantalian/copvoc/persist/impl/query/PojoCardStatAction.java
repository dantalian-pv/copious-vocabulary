package ru.dantalian.copvoc.persist.impl.query;

import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.api.model.CardStatType;
import ru.dantalian.copvoc.persist.api.stats.StatAction;
import ru.dantalian.copvoc.persist.impl.model.PojoCardStat;

public class PojoCardStatAction extends PojoCardStat implements CardStatAction {

	private final StatAction action;

	public PojoCardStatAction(final String aName, final Object aValue, final CardStatType aType,
			final StatAction aAction) {
		super(aName, aValue, aType);
		action = aAction;
	}

	@Override
	public StatAction getAction() {
		return action;
	}

}
