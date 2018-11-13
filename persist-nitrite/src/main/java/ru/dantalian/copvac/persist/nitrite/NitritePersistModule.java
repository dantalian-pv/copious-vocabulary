package ru.dantalian.copvac.persist.nitrite;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.object.db.OrientDBObject;

import ru.dantalian.copvac.persist.api.PersistLanguageManager;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.nitrite.managers.NitritePersistLanguageManager;
import ru.dantalian.copvac.persist.nitrite.managers.NitritePersistPrincipalManager;
import ru.dantalian.copvac.persist.nitrite.providers.DbProvider;
import ru.dantalian.copvac.persist.nitrite.providers.DbSessionProvider;

public class NitritePersistModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PersistPrincipalManager.class).to(NitritePersistPrincipalManager.class);
		bind(PersistLanguageManager.class).to(NitritePersistLanguageManager.class);
		bind(OrientDBObject.class).toProvider(DbProvider.class).in(Scopes.SINGLETON);
		bind(ODatabaseObject.class).toProvider(DbSessionProvider.class).in(Scopes.SINGLETON);
	}

}
