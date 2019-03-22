package ru.dantalian.copvoc.persist.api.model;

public interface CardStat {

	String getName();

	<T> T getValue();

	CardStatType getType();

}
