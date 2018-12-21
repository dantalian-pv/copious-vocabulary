package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;

public interface PersistCardManager {

	Card createCard(String aUser, UUID aVocabularyId, Map<String, String> aContent) throws PersistException;

	void updateCard(String aUser, UUID aId, Map<String, String> aContent) throws PersistException;

	Card getCard(String aUser, UUID aId) throws PersistException;

	void deleteCard(String aUser, UUID aId) throws PersistException;

	List<Card> queryCards(String aUser, CardsQuery aQuery) throws PersistException;

}
