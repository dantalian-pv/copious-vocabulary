package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface CardBatch {

	UUID getId();

	String getName();

	String getDescription();

	String getUser();

	Language getSource();

	Language getTarget();

}
