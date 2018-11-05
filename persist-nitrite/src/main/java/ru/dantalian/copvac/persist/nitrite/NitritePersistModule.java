package ru.dantalian.copvac.persist.nitrite;

import org.dizitart.no2.Nitrite;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import ru.dantalian.copvac.persist.api.PersistLanguageManager;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.nitrite.managers.NitritePersistLanguageManager;
import ru.dantalian.copvac.persist.nitrite.managers.NitritePersistPrincipalManager;
import ru.dantalian.copvac.persist.nitrite.providers.DbProvider;

public class NitritePersistModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PersistPrincipalManager.class).to(NitritePersistPrincipalManager.class);
		bind(PersistLanguageManager.class).to(NitritePersistLanguageManager.class);
		bind(Nitrite.class).toProvider(DbProvider.class).in(Scopes.SINGLETON);
	}

}
