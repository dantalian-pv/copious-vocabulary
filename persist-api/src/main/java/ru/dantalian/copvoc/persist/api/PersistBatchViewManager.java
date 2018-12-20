package ru.dantalian.copvoc.persist.api;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardBatchView;

public interface PersistBatchViewManager {

	CardBatchView createBatchView(UUID aBatchId, String aCss, String aFrontTpl,
			String aBackTpl) throws PersistException;

	void updateBatchView(UUID aId, String aCss, String aFrontTpl,
			String aBackTpl) throws PersistException;

	CardBatchView getBatchView(UUID aId) throws PersistException;

	CardBatchView getBatchViewByBatchId(UUID aBatchId) throws PersistException;

}
