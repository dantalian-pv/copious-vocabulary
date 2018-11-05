package ru.dantalian.copvac.persist.nitrite.providers;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.exceptions.NitriteIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.dantalian.copvac.persist.nitrite.NitriteSettings;

@Singleton
public class DbProvider implements Provider<Nitrite> {

	private static final Logger logger = LoggerFactory.getLogger(DbProvider.class);

	@Inject
	private NitriteSettings settings;

	@Override
	public Nitrite get() {
		final File dbPath = this.settings.getDataDir().toPath().resolve("user.db").toFile();
		try {
			final Nitrite db = Nitrite.builder()
	        .compressed()
	        .filePath(dbPath)
	        .openOrCreate();
			return db;
		} catch (final NitriteIOException e) {
			logger.error("Creating DB session failed", e);
			throw new IllegalStateException("Unable to open database in " + dbPath.toString());
		}
	}

}
