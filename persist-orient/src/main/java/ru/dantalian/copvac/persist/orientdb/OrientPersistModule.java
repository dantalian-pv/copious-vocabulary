package ru.dantalian.copvac.persist.orientdb;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.object.db.OrientDBObject;

import ru.dantalian.copvac.persist.api.PersistLanguageManager;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.orientdb.managers.OrientPersistLanguageManager;
import ru.dantalian.copvac.persist.orientdb.managers.OrientPersistPrincipalManager;
import ru.dantalian.copvac.persist.orientdb.providers.DbProvider;
import ru.dantalian.copvac.persist.orientdb.providers.DbSessionProvider;

public class OrientPersistModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PersistPrincipalManager.class).to(OrientPersistPrincipalManager.class);
		bind(PersistLanguageManager.class).to(OrientPersistLanguageManager.class);
		bind(OrientDBObject.class).toProvider(DbProvider.class).in(Scopes.SINGLETON);
		bind(ODatabaseObject.class).toProvider(DbSessionProvider.class).in(Scopes.SINGLETON);
	}

}
