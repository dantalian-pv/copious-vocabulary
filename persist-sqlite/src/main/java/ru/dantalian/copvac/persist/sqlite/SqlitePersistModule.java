package ru.dantalian.copvac.persist.sqlite;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import ru.dantalian.copvac.persist.api.PersistManager;
import ru.dantalian.copvac.persist.sqlite.providers.SessionProvider;

public class SqlitePersistModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PersistManager.class).to(SqlitePersistManager.class);
		bind(EntityManager.class).toProvider(SessionProvider.class).in(Scopes.SINGLETON);
	}

}
