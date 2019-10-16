package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.api.model.Training;
import ru.dantalian.copvoc.persist.api.query.Query;

public interface PersistTrainingManager {

	Training createTraining(final String aUser, UUID aVocabularyId, Optional<Integer> aCount,
			final Map<String, CardStat> aStatsMap) throws PersistException;

	Training getTraining(final String aUser, UUID aTrainigId) throws PersistException;

	List<Training> queryTrainings(final String aUser, Query aQuery) throws PersistException;

	Training finishTraining(final String aUser, UUID aTrainigId) throws PersistException;

	UUID firstCard(final String aUser, UUID aTrainigId) throws PersistException;

	UUID nextCard(final String aUser, UUID aTrainigId) throws PersistException;

	Map<String, CardStat> getStatsForCard(final String aUser, UUID aTrainigId, UUID aCardId) throws PersistException;

	void updateStatsForCard(final String aUser, UUID aTrainigId, UUID aCardId,
			Map<String, CardStat> aStats) throws PersistException;

	void updateStatForCard(final String aUser, UUID aTrainigId, UUID aCardId, CardStatAction aAction) throws PersistException;

}
