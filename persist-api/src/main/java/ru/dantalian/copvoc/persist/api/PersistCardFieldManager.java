package ru.dantalian.copvoc.persist.api;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public interface PersistCardFieldManager {

	CardField createField(UUID aBatchId, String aName, CardFiledType aType) throws PersistException;

	CardField getField(UUID aBatchId, String aName) throws PersistException;

	void deleteField(UUID aBatchId, String aName) throws PersistException;

	List<CardField> listFields(UUID aBatchId) throws PersistException;

}
