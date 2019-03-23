package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;

public interface PersistVocabularyManager {

	Vocabulary createVocabulary(String aUser, final String aName, final String aDescription,
			Language aSource, Language aTarget) throws PersistException;

	void updateVocabulary(String aUser, Vocabulary aVocabulary) throws PersistException;

	Vocabulary getVocabulary(String aUser, UUID aId) throws PersistException;

	Vocabulary queryVocabulary(String aUser, String aName) throws PersistException;

	List<Vocabulary> listVocabularies(String aUser) throws PersistException;

	void deleteVocabulary(String aUser, UUID aId) throws PersistException;

}
