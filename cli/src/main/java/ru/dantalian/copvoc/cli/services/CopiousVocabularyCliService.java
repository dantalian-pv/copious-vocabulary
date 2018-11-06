package ru.dantalian.copvoc.cli.services;

import java.io.Closeable;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvoc.cli.CopiousVocabularyCliException;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.CoreService;
import ru.dantalian.copvoc.core.managers.PrincipalManager;

@Singleton
public class CopiousVocabularyCliService implements Closeable {

	private static final String ROOT = "root";

	@Inject
	private CoreService core;

	private PrincipalManager principalManager;

	private Principal rootUser;

	@Inject
	public void init() {
		principalManager = core.getPrincipalManager();
	}

	public void execute() throws CopiousVocabularyCliException {
		try {
			rootUser = principalManager.getPrincipalByName(ROOT);
			if (rootUser == null) {
				rootUser = principalManager.createPrincipal(ROOT, null);
			}
		} catch (final CoreException e) {
			throw new CopiousVocabularyCliException("Something happend", e);
		}
	}

	@Override
	public void close() throws IOException {
		core.close();
	}

}
