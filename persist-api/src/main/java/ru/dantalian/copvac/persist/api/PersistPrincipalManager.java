package ru.dantalian.copvac.persist.api;

import java.io.Closeable;

import ru.dantalian.copvac.persist.api.model.personal.Principal;

public interface PersistPrincipalManager extends Closeable {

	Principal createPrincipal(String aName, String aDescription) throws PersistException;

	Principal getPrincipalByName(String aName) throws PersistException;

}
