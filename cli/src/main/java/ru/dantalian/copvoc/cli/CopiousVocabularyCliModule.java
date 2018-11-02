package ru.dantalian.copvoc.cli;

import java.io.File;
import java.nio.file.Paths;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import ru.dantalian.copvac.persist.nitrite.NitriteConstants;

public class CopiousVocabularyCliModule extends AbstractModule {

	private final File configDir;
	private final File dataDir;

	public CopiousVocabularyCliModule(final CliOptions aCliOptions) throws CopiousVocabularyCliException {
		this.configDir = validate(aCliOptions.getConfig());
		this.dataDir = validate(aCliOptions.getData());
	}

	@Override
	protected void configure() {
		bind(File.class)
			.annotatedWith(Names.named(NitriteConstants.CONFIG_DIR))
			.toInstance(configDir);
		bind(File.class)
			.annotatedWith(Names.named(NitriteConstants.DATA_DIR))
			.toInstance(dataDir);
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
