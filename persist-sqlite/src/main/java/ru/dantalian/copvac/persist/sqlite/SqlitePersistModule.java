package ru.dantalian.copvac.persist.sqlite;

import org.springframework.jdbc.core.JdbcTemplate;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import ru.dantalian.copvac.persist.api.PersistLanguageManager;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.sqlite.managers.SqlitePersistLanguageManager;
import ru.dantalian.copvac.persist.sqlite.managers.SqlitePersistPrincipalManager;
import ru.dantalian.copvac.persist.sqlite.providers.DbProvider;

public class SqlitePersistModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PersistPrincipalManager.class).to(SqlitePersistPrincipalManager.class);
		bind(PersistLanguageManager.class).to(SqlitePersistLanguageManager.class);
		bind(JdbcTemplate.class).toProvider(DbProvider.class).in(Scopes.SINGLETON);
	}

}
