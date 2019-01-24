package ru.dantalian.copvoc.persist.sqlite.providers;

import java.io.File;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.sqlite.SQLiteDataSource;

import ru.dantalian.copvoc.persist.sqlite.config.SqliteSettings;

@Component
public class DbProvider extends AbstractFactoryBean<JdbcTemplate> {

	@Autowired
	private SqliteSettings settings;

	@Override
	public Class<?> getObjectType() {
		return JdbcTemplate.class;
	}

	@Override
	protected JdbcTemplate createInstance() throws Exception {
		settings.getDataDir().mkdirs();
		final File dbPath = settings.getDataDir().toPath().resolve("user_db").toFile();
		final SQLiteDataSource dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + dbPath.getPath());
		// Schema migration
		final Flyway flyway = Flyway.configure().dataSource(dataSource).load();
		flyway.migrate();

		// Init JdbcTemplate
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate;
	}

}
