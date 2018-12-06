package ru.dantalian.copvoc.core.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistPrincipalManager;
import ru.dantalian.copvoc.persist.api.model.personal.Principal;

@Service
public class PrincipalManager {

	@Autowired
	private PersistPrincipalManager principalPersist;

	public Principal getPrincipalByName(final String aName) throws CoreException {
		try {
			return principalPersist.getPrincipalByName(aName);
		} catch (final PersistException e) {
			throw new CoreException("Failed to get a user by name", e);
		}
	}

	public Principal createPrincipal(final String aName, final String aDescription) throws CoreException {
		try {
			return principalPersist.createPrincipal(aName, aDescription);
		} catch (final PersistException e) {
			throw new CoreException("Failed to get a user", e);
		}
	}

}
