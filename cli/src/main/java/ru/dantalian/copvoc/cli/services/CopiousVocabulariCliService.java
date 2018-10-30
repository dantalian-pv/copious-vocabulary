package ru.dantalian.copvoc.cli.services;

import java.io.File;
import java.nio.file.Paths;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;
import ru.dantalian.copvoc.cli.CliOptions;
import ru.dantalian.copvoc.cli.CopiousVocabularyCliException;

@Singleton
public class CopiousVocabulariCliService {

	private static final Logger logger = LoggerFactory.getLogger(CopiousVocabulariCliService.class);

	public void execute(final String[] aArgs) throws CopiousVocabularyCliException {
		try {
			final CliOptions cliOptions = CommandLine.populateCommand(new CliOptions(), aArgs);
			if (cliOptions.isHelp()) {
				CommandLine.usage(new CliOptions(), System.out);
				return;
			}
			final File configDir = validate(cliOptions.getConfig());
			final File dataDir = validate(cliOptions.getData());
			run(configDir, dataDir);
			System.out.println("Application closed");
		} catch (final MissingParameterException e) {
			System.err.println(e.getMessage());
			CommandLine.usage(new CliOptions(), System.out);
		} catch (final CopiousVocabularyCliException e) {
			logger.error(e.getMessage(), e);
			System.err.println(e.getMessage());
		}
	}

	private void run(final File configDir, final File dataDir) throws CopiousVocabularyCliException {
		// TODO Auto-generated method stub

	}

	private File validate(final File aDir) {
		File dir = aDir;
		if (!dir.isAbsolute()) {
			final String userHome = System.getProperty("user.home");
			dir = Paths.get(userHome, dir.getPath()).toFile();
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

}
