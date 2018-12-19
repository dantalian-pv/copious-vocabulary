package ru.dantalian.copvoc.core.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.persist.api.PersistBatchManager;
import ru.dantalian.copvoc.persist.api.PersistBatchViewManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardBatch;
import ru.dantalian.copvoc.persist.api.model.CardBatchView;
import ru.dantalian.copvoc.persist.api.model.Language;

@Service
public class BatchManager {

	@Autowired
	private PersistBatchManager persistBatchManager;

	@Autowired
	private PersistBatchViewManager persistViewManager;

	public void initDefaultView(final UUID aBatchId) throws PersistException {
		try (InputStream cssStream = this.getClass().getClassLoader()
				.getResourceAsStream(CoreConstants.DEFAULT_CARD_BATCH_VIEW_CSS);
				InputStream frontStream = this.getClass().getClassLoader()
						.getResourceAsStream(CoreConstants.DEFAULT_CARD_BATCH_VIEW_FRONT);
				InputStream backStream = this.getClass().getClassLoader()
						.getResourceAsStream(CoreConstants.DEFAULT_CARD_BATCH_VIEW_BACK)) {
			final String css = new BufferedReader(new InputStreamReader(cssStream))
				  .lines().collect(Collectors.joining("\n"));
			final String front = new BufferedReader(new InputStreamReader(frontStream))
				  .lines().collect(Collectors.joining("\n"));
			final String back = new BufferedReader(new InputStreamReader(backStream))
				  .lines().collect(Collectors.joining("\n"));
			persistViewManager.createBatchView(aBatchId, css, front, back);
		} catch (final IOException e) {
			throw new PersistException("Failed to init view", e);
		}
	}

	public CardBatch createBatch(final String aUser, final String aName, final String aDescription,
			final Language aSource, final Language aTarget) throws PersistException {
		return persistBatchManager.createBatch(aUser, aName, aDescription, aSource, aTarget);
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

	public CardBatchView createBatchView(final UUID aBatchId, final String aCss, final String aFrontTpl,
			final String aBackTpl) throws PersistException {
		return persistViewManager.createBatchView(aBatchId, aCss, aFrontTpl, aBackTpl);
	}

	public void updateBatchView(final UUID aId, final String aCss, final String aFrontTpl,
			final String aBackTpl) throws PersistException {
		persistViewManager.updateBatchView(aId, aCss, aFrontTpl, aBackTpl);
	}

	public CardBatchView getBatchView(final UUID aId) throws PersistException {
		return persistViewManager.getBatchView(aId);
	}

}
