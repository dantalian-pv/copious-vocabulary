package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardBatch;
import ru.dantalian.copvoc.persist.api.model.Language;

public interface PersistBatchManager {

	CardBatch createBatch(String aUser, final String aName, final String aDescription,
			Language aSource, Language aTarget) throws PersistException;

	void updateBatch(String aUser, CardBatch aCardBatch) throws PersistException;

	CardBatch getBatch(String aUser, UUID aId) throws PersistException;

	CardBatch queryBatch(String aUser, String aName) throws PersistException;

	List<CardBatch> listBatches(String aUser) throws PersistException;

}
