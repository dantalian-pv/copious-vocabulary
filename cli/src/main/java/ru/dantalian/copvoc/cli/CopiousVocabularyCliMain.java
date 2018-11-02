package ru.dantalian.copvoc.cli;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;
import ru.dantalian.copvac.persist.nitrite.NitritePersistModule;
import ru.dantalian.copvoc.cli.services.CopiousVocabularyCliService;
import ru.dantalian.copvoc.core.CoreModule;

public class CopiousVocabularyCliMain {

	private static final Logger logger = LoggerFactory.getLogger(CopiousVocabularyCliMain.class);

	public static void main(final String[] aArgs) {
		try {
			final CliOptions cliOptions = CommandLine.populateCommand(new CliOptions(), aArgs);
			if (cliOptions.isHelp()) {
				CommandLine.usage(new CliOptions(), System.out);
				return;
			}
			final Injector injector = Guice.createInjector(
				new NitritePersistModule(),
				new CoreModule(),
				new CopiousVocabularyCliModule(cliOptions)
			);
			final CopiousVocabularyCliService cliService = injector.getInstance(CopiousVocabularyCliService.class);

			try {
				cliService.execute();
			} finally {
				cliService.close();
				System.out.println("Application closed");
			}
		} catch (final MissingParameterException e) {
			System.err.println(e.getMessage());
			CommandLine.usage(new CliOptions(), System.out);
		} catch (final CopiousVocabularyCliException | IOException e) {
			logger.error("Failed to run application", e);
			System.exit(1);
		}
	}

}
