package ru.dantalian.copvoc.persist.api.model;

import ru.dantalian.copvoc.persist.api.stats.StatAction;

public interface CardStatAction extends CardStat {

	StatAction getAction();

}
