package ru.dantalian.copvac.persist.nitrite;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ru.dantalian.copvac.persist.api.utils.Validator;

@Singleton
public class NitriteSettings {

	@Inject
	@Named(NitriteConstants.CONFIG_DIR)
	private File configDir;

	@Inject
	@Named(NitriteConstants.DATA_DIR)
	private File dataDir;

	@PostConstruct
	public void init() {
		Validator.checkNotNull(configDir, "Please set configDir");
		Validator.checkNotNull(dataDir, "Please set dataDir");
	}

	public File getConfigDir() {
		return configDir;
	}

	public File getDataDir() {
		return dataDir;
	}

}
