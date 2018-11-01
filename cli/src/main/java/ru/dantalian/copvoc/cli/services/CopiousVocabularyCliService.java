package ru.dantalian.copvoc.cli.services;

import java.io.Closeable;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvoc.cli.CopiousVocabularyCliException;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.CoreService;

@Singleton
public class CopiousVocabularyCliService implements Closeable {

	private static final String ROOT = "root";

	@Inject
	private CoreService core;

	private Principal rootUser;

	public void execute() throws CopiousVocabularyCliException {
		try {
			rootUser = core.getPrincipalByName(ROOT);
			if (rootUser == null) {
				rootUser = core.createPrincipal(ROOT, null);
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
