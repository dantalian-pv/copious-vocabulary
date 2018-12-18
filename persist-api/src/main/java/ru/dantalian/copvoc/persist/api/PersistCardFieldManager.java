package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public interface PersistCardFieldManager {

	CardField createField(UUID aBatchId, String aName, String aDisplayName, CardFiledType aType) throws PersistException;

	CardField updateField(UUID aId, String aDisplayName) throws PersistException;

	CardField getField(UUID aId) throws PersistException;

	void deleteField(UUID aId) throws PersistException;

	List<CardField> listFields(UUID aBatchId) throws PersistException;

}