package ru.dantalian.copvac.persist.api;

import java.io.Closeable;
import java.util.UUID;

import ru.dantalian.copvac.persist.api.model.personal.Principal;

public interface PersistPrincipalManager extends Closeable {

	Principal getPrincipal(UUID aId) throws PersistException;

	Principal getPrincipal(UUID aId, String aPasswd) throws PersistException;

	Principal createPrincipal(String aName, String aDescription) throws PersistException;

	Principal getPrincipalByName(String aName) throws PersistException;

}
