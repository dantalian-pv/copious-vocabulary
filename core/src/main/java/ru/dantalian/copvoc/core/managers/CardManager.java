package ru.dantalian.copvoc.core.managers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;

@Service
public class CardManager implements PersistCardManager {

	@Autowired
	private PersistCardManager persistsCardManager;

	@Override
	public Card createCard(final UUID aBatchId, final Map<String, String> aContent) throws PersistException {
		return persistsCardManager.createCard(aBatchId, aContent);
	}

	@Override
	public Card getCard(final UUID aId) throws PersistException {
		return persistsCardManager.getCard(aId);
	}

	@Override
	public void deleteCard(final UUID aId) throws PersistException {
		persistsCardManager.deleteCard(aId);
	}

	@Override
	public List<Card> queryCards(final CardsQuery aQuery) throws PersistException {
		return persistsCardManager.queryCards(aQuery);
	}

	@Override
	public void updateCard(final UUID aId, final Map<String, String> aContent) throws PersistException {
		persistsCardManager.updateCard(aId, aContent);
	}

}
