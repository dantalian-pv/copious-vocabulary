package ru.dantalian.copvac.persist.api.model;

public interface LanguageRepresentation {

	Language getTarget();

	Language getSource();

	String getRepresentation();

}
