package ru.dantalian.copvac.persist.orientdb.providers;

import java.io.File;
import java.util.logging.Handler;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.orientechnologies.common.log.OLogFormatter;
import com.orientechnologies.common.log.OLogManager;
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
			installLogger();
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

	private void installLogger() {
		// Some magic to replace default java.util.logger with logback from OriendDB
		SLF4JBridgeHandler.install();
		OLogManager.instance().installCustomFormatter();
		final java.util.logging.Logger log = java.util.logging.Logger.getLogger("");
		final Handler[] handlers = log.getHandlers();
		for(final Handler handler: handlers) {
			if (handler.getFormatter() instanceof OLogFormatter) {
				log.removeHandler(handler);
			}
		}
	}

}
