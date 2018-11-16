package ru.dantalian.copvac.persist.orientdb.providers;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.object.db.OrientDBObject;

import ru.dantalian.copvac.persist.orientdb.OrientSettings;

@Singleton
public class DbProvider implements Provider<OrientDBObject> {

	private static final Logger logger = LoggerFactory.getLogger(DbProvider.class);

	@Inject
	private OrientSettings settings;

	@Override
	public OrientDBObject get() {
		final File dbPath = this.settings.getDataDir().toPath().resolve("user_db").toFile();
		try {
			final OrientDBObject orientDbObject = new OrientDBObject("embedded:"
					+ this.settings.getDataDir().getPath(), "admin", "admin", OrientDBConfig.defaultConfig());
			if (!orientDbObject.exists("user_db")) {
				orientDbObject.create("user_db", ODatabaseType.PLOCAL);
			}
			return orientDbObject;
		} catch (final Exception e) {
			logger.error("Creating DB session failed", e);
			throw new IllegalStateException("Unable to open database in " + dbPath.toString());
		}
	}

}
