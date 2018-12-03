package ru.dantalian.copvoc.persist.api.model;

import java.util.UUID;

public interface CardBatchView {

	UUID getId();

	UUID getBatchId();

	String getCss();

	String getFront();

	String getBack();

}
