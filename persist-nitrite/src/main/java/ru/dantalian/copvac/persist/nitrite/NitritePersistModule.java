package ru.dantalian.copvac.persist.nitrite;

import org.dizitart.no2.Nitrite;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import ru.dantalian.copvac.persist.api.PersistManager;
import ru.dantalian.copvac.persist.nitrite.providers.SessionProvider;

public class NitritePersistModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PersistManager.class).to(NitritePersistManager.class);
		bind(Nitrite.class).toProvider(SessionProvider.class).in(Scopes.SINGLETON);
	}

}
