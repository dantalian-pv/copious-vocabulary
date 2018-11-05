package ru.dantalian.copvac.persist.api.model;

import java.util.List;
import java.util.UUID;

public interface CardBatch {

	UUID getId();

	String getUserId();

	Language getSource();

	Language getTarget();

	List<String> getCardFieldIds();

	boolean isPublic();

}
