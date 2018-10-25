package ru.dantalian.copvac.persist.api.model;

import java.util.List;

public interface CardBatchSettings {

	String getId();

	String getBatchId();

	Language getLanguage();

	List<String> getCardFieldIds();

}
