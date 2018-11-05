package ru.dantalian.copvac.persist.nitrite;

import org.dizitart.no2.Nitrite;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.nitrite.providers.SessionProvider;

public class NitritePersistModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PersistPrincipalManager.class).to(NitritePersistPrincipalManager.class);
		bind(Nitrite.class).toProvider(SessionProvider.class).in(Scopes.SINGLETON);
	}

}
