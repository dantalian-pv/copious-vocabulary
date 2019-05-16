package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface Vocabulary {

	UUID getId();

	String getName();

	String getDescription();

	String getUser();

	Language getSource();

	Language getTarget();

	boolean isShared();

}
