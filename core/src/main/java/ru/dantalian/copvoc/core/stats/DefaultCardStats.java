package ru.dantalian.copvoc.core.stats;

import ru.dantalian.copvoc.persist.api.model.CardStatType;

public enum DefaultCardStats {

	LAST_VISIT("lastVisit", CardStatType.DATE),
	SUCESS("success", CardStatType.LONG),
	FAIL("fail", CardStatType.LONG),
	SKIP("skip", CardStatType.LONG),
	VISITS("visits", CardStatType.LONG),
	SHARED("shared", CardStatType.LONG),
	VOTE_UP("voteUp", CardStatType.LONG),
	VOTE_DOWN("voteDown", CardStatType.LONG);

	private final String name;
	private final CardStatType type;

	private DefaultCardStats(final String aName, final CardStatType aType) {
		name = aName;
		type = aType;
	}

	public String getName() {
		return name;
	}

	public CardStatType getType() {
		return type;
	}

}
