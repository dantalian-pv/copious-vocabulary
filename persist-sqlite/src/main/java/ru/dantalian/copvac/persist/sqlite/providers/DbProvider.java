package ru.dantalian.copvac.persist.sqlite.providers;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteDataSource;

import ru.dantalian.copvac.persist.sqlite.SqliteSettings;

@Singleton
public class DbProvider implements Provider<JdbcTemplate> {

	private static final Logger logger = LoggerFactory.getLogger(DbProvider.class);

	@Inject
	private SqliteSettings settings;

	@Override
	public JdbcTemplate get() {
		final File dbPath = this.settings.getDataDir().toPath().resolve("user_db").toFile();
		try {
			final SQLiteDataSource dataSource = new SQLiteDataSource();
			dataSource.setUrl("jdbc:sqlite:" + dbPath.getPath());
			// Schema migration
			final Flyway flyway = Flyway.configure().dataSource(dataSource).load();
			flyway.migrate();

			// Init JdbcTemplate
			final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			return jdbcTemplate;
		} catch (final Exception e) {
			logger.error("Creating DB session failed", e);
			throw new IllegalStateException("Unable to open database in " + dbPath.toString());
		}
	}

}
