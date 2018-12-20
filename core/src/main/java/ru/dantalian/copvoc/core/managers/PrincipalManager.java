package ru.dantalian.copvoc.core.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistPrincipalManager;
import ru.dantalian.copvoc.persist.api.model.Principal;

@Service
public class PrincipalManager implements PersistPrincipalManager {

	@Autowired
	private PersistPrincipalManager principalPersist;

	@Override
	public Principal getPrincipalByName(final String aName) throws PersistException {
		return principalPersist.getPrincipalByName(aName);
	}

	@Override
	public Principal createPrincipal(final String aName, final String aDescription)
			throws PersistException {
		return principalPersist.createPrincipal(aName, aDescription);
	}

	@Override
	public String getPasswordFor(final String aUsername) throws PersistException {
		return principalPersist.getPasswordFor(aUsername);
	}

	@Override
	public void storePasswordFor(final String aUsername, final CharSequence aEncryptedPassword)
			throws PersistException {
		principalPersist.storePasswordFor(aUsername, aEncryptedPassword);
	}

}
