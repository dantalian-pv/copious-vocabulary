package ru.dantalian.copvoc.persist.api;

import java.io.Closeable;

import ru.dantalian.copvoc.persist.api.model.personal.Principal;

public interface PersistPrincipalManager extends Closeable {

	Principal createPrincipal(String aName, String aDescription) throws PersistException;

	Principal getPrincipalByName(String aName) throws PersistException;

}
