package ru.dantalian.copvoc.core.managers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistBatchManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardBatch;
import ru.dantalian.copvoc.persist.api.model.Language;

@Service
public class BatchManager {

	@Autowired
	private PersistBatchManager persistBatchManager;

	public CardBatch createBatch(final String aUser, final String aName, final String aDescription,
			final Language aSource, final Language aTarget) throws PersistException {
		final CardBatch batch = persistBatchManager.createBatch(aUser, aName, aDescription, aSource, aTarget);

		return batch;
	}

	public void updateBatch(final String aUser, final CardBatch aCardBatch) throws PersistException {
		persistBatchManager.updateBatch(aUser, aCardBatch);
	}

	public CardBatch getBatch(final String aUser, final UUID aId) throws PersistException {
		return persistBatchManager.getBatch(aUser, aId);
	}

	public CardBatch queryBatch(final String aUser, final String aName) throws PersistException {
		return persistBatchManager.queryBatch(aUser, aName);
	}

	public List<CardBatch> listBatches(final String aUser) throws PersistException {
		return persistBatchManager.listBatches(aUser);
	}

}
