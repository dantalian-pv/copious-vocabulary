package ru.dantalian.copvoc.core.managers;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvoc.core.CoreException;

@Singleton
public class PrincipalManager implements Closeable {

	@Inject
	private PersistPrincipalManager principalPersist;

	public Principal getPrincipal(final UUID aId, final String aPasswd) throws CoreException {
		try {
			final Principal principal = principalPersist.getPrincipal(aId, aPasswd);
			if (principal == null) {
				throw new CoreException("User not found", true);
			}
			return principal;
		} catch (final PersistException e) {
			throw new CoreException("Failed to get a user", e);
		}
	}

	public Principal getPrincipal(final UUID aId) throws CoreException {
		try {
			return principalPersist.getPrincipal(aId);
		} catch (final PersistException e) {
			throw new CoreException("Failed to get a user", e);
		}
	}

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

	@Override
	public void close() throws IOException {
		principalPersist.close();
	}

}
