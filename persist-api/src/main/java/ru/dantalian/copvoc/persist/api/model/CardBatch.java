package ru.dantalian.copvoc.persist.api.model;

import java.util.List;
import java.util.UUID;

public interface CardBatch {

	UUID getId();

	UUID getUserId();

	Language getSource();

	Language getTarget();

	List<UUID> getFieldIds();

}
