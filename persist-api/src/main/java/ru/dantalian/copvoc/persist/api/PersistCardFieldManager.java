package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public interface PersistCardFieldManager {

	CardField createField(String aUser, UUID aVocabularyId, String aName, CardFiledType aType, Integer aOrder,
			boolean aSystem) throws PersistException;

	CardField getField(String aUser, UUID aVocabularyId, String aName) throws PersistException;

	void deleteField(String aUser, UUID aVocabularyId, String aName) throws PersistException;

	List<CardField> listFields(String aUser, UUID aVocabularyId) throws PersistException;

}
