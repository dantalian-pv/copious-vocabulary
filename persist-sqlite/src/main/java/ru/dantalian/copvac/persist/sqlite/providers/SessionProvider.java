package ru.dantalian.copvac.persist.sqlite.providers;

import java.nio.file.Path;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.dantalian.copvac.persist.sqlite.SqliteSettings;
import ru.dantalian.copvac.persist.sqlite.hibernate.model.HibPrincipal;
import ru.dantalian.copvac.persist.sqlite.hibernate.model.PrincipalCredentials;

@Singleton
public class SessionProvider implements Provider<EntityManager> {

	private static final Logger logger = LoggerFactory.getLogger(SessionProvider.class);

	@Inject
	private SqliteSettings settings;

	private SessionFactory sessionFactory;

	@PreDestroy
	public void close() {
		try {
			if (this.sessionFactory != null) {
				this.sessionFactory.close();
			}
		} catch (final HibernateException e) {
			throw new IllegalStateException("Failed to close DB session", e);
		}
	}

	@Override
	public EntityManager get() {
		final Path dbPath = this.settings.getDataDir().toPath().resolve("user.db");
		try {
			Class.forName("org.sqlite.JDBC");
			final Properties props = new Properties();
			props.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
			props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");
			props.setProperty("hibernate.connection.url", "jdbc:sqlite://" + dbPath.toString());
			props.setProperty("hibernate.connection.autocommit", "true");
			props.setProperty("hibernate.hbm2ddl.auto", "validate or create");
			final Configuration cfg = new Configuration()
			    .addAnnotatedClass(HibPrincipal.class)
			    .addAnnotatedClass(PrincipalCredentials.class)
			    .addProperties(props);
			this.sessionFactory = cfg.buildSessionFactory();
			return this.sessionFactory.createEntityManager();
		} catch (final HibernateException | ClassNotFoundException e) {
			logger.error("Creating DB session failed", e);
			throw new IllegalStateException("Unable to open database in " + dbPath.toString());
		}
	}

}
