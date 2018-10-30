package ru.dantalian.copvoc.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ru.dantalian.copvoc.cli.services.CopiousVocabulariCliService;

public class CopiousVocabularyCliMain {

	private static final Logger logger = LoggerFactory.getLogger(CopiousVocabularyCliMain.class);

	public static void main(final String[] args) {
		final Injector injector = Guice.createInjector(
			new CopiousVocabularyCoreModule(),
			new CopiousVocabularyCliModule()
		);
		final CopiousVocabulariCliService cliService = injector.getInstance(CopiousVocabulariCliService.class);
		try {
			cliService.execute(args);
		} catch (final CopiousVocabularyCliException e) {
			logger.error("Failed to run application", e);
			System.exit(1);
		}
	}

}
