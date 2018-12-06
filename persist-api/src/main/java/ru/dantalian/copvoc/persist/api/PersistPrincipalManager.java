package ru.dantalian.copvoc.persist.api;

import ru.dantalian.copvoc.persist.api.model.personal.Principal;

public interface PersistPrincipalManager {

	Principal createPrincipal(String aName, String aDescription) throws PersistException;

	Principal getPrincipalByName(String aName) throws PersistException;

	String getPasswordFor(String aUsername) throws PersistException;

	void storePasswordFor(String aUsername, CharSequence aEncryptedPassword) throws PersistException;

}
