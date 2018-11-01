package ru.dantalian.copvac.persist.api;

import java.io.Closeable;

import ru.dantalian.copvac.persist.api.model.personal.Principal;

public interface PersistManager extends Closeable {

	Principal getPrincipal(String aId) throws PersistException;

	Principal getPrincipal(String aId, String aPasswd) throws PersistException;

	Principal createPrincipal(String aName, String aDescription) throws PersistException;

	Principal getPrincipalByName(String aName) throws PersistException;

}
