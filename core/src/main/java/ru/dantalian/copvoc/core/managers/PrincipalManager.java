package ru.dantalian.copvoc.core.managers;

import java.io.Closeable;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvoc.core.CoreException;

@Service
public class PrincipalManager implements Closeable {

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

	@Override
	public void close() throws IOException {
		principalPersist.close();
	}

}
