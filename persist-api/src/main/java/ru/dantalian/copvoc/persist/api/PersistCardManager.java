package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;

public interface PersistCardManager {

	Card createCard(UUID aBatchId, Map<String, String> aContent) throws PersistException;

	void updateCard(UUID aId, Map<String, String> aContent) throws PersistException;

	Card getCard(UUID aId) throws PersistException;

	void deleteCard(UUID aId) throws PersistException;

	List<Card> queryCards(CardsQuery aQuery) throws PersistException;

}
