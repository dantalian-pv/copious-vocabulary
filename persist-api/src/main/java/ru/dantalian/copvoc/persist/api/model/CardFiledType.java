package ru.dantalian.copvoc.persist.api.model;

public enum CardFiledType {

	STRING,
	TEXT,
	MARKUP,
	ANSWER;

	public static boolean isText(final CardFiledType aType) {
		return aType == TEXT || aType == MARKUP;
	}

	public static boolean isString(final CardFiledType aType) {
		return aType == STRING || aType == ANSWER;
	}

}
