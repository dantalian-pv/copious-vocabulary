package ru.dantalian.copvoc.persist.api;

import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.query.QueryResult;

public interface PersistCardManager {

	Card createCard(String aUser, UUID aVocabularyId,
			String aSource,
			Map<String, String> aContent,
			Map<String, CardStat> aStatsMap) throws PersistException;

	Card updateCard(String aUser, UUID aVocabularyId, UUID aId, Map<String, String> aContent) throws PersistException;

	Card updateStats(String aUser, UUID aVocabularyId, UUID aId, Map<String, CardStat> aStatsMap) throws PersistException;

	Card getCard(String aUser, UUID aVocabularyId, UUID aId) throws PersistException;

	void deleteCard(String aUser, UUID aVocabularyId, UUID aId) throws PersistException;

	void deleteAllCards(String aUser, UUID aVocabularyId) throws PersistException;

	QueryResult<Card> queryCards(String aUser, Query aQuery) throws PersistException;

	void updateStatForCard(final String aUser, UUID aVocabularyId, UUID aCardId, CardStatAction aAction) throws PersistException;

}
