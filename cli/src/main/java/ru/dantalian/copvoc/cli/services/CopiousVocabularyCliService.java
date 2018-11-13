package ru.dantalian.copvoc.cli.services;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.dantalian.copvac.persist.api.model.Language;
import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvoc.cli.CopiousVocabularyCliException;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.CoreService;
import ru.dantalian.copvoc.core.managers.LanguageManager;
import ru.dantalian.copvoc.core.managers.PrincipalManager;

@Singleton
public class CopiousVocabularyCliService implements Closeable {

	private static final Logger logger = LoggerFactory.getLogger(CopiousVocabularyCliService.class);

	private static final String ROOT = "root";

	@Inject
	private CoreService core;

	private PrincipalManager principalManager;

	private LanguageManager languageManager;

	private Principal rootUser;

	@Inject
	public void init() {
		principalManager = core.getPrincipalManager();
		languageManager = core.getLanguageManager();
	}

	public void execute() throws CopiousVocabularyCliException {
		try {
			rootUser = principalManager.getPrincipalByName(ROOT);
			if (rootUser == null) {
				rootUser = principalManager.createPrincipal(ROOT, null);
			}
			final List<Language> languages = languageManager.initLanguages();
			for (final Language lang: languages) {
				logger.info("{}", lang);
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
