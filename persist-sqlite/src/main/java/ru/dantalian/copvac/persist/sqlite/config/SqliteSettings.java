package ru.dantalian.copvac.persist.sqlite.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqliteSettings {

	@Value("${config.dir}")
	private File configDir;

	@Value("${data.dir}")
	private File dataDir;

	public File getConfigDir() {
		return configDir;
	}

	public void setConfigDir(final File aConfigDir) {
		configDir = aConfigDir;
	}

	public File getDataDir() {
		return dataDir;
	}

	public void setDataDir(final File aDataDir) {
		dataDir = aDataDir;
	}

}
