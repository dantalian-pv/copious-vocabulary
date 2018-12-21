package ru.dantalian.copvoc.persist.api;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.VocabularyView;

public interface PersistVocabularyViewManager {

	VocabularyView createVocabularyView(String aUser, UUID aVocabularyId, String aCss, String aFrontTpl,
			String aBackTpl) throws PersistException;

	void updateVocabularyView(String aUser, UUID aVocabularyId, String aCss, String aFrontTpl,
			String aBackTpl) throws PersistException;

	VocabularyView getVocabularyView(String aUser, UUID aVocabularyId) throws PersistException;

}
