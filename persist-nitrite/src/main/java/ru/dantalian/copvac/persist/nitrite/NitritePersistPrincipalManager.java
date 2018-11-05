package ru.dantalian.copvac.persist.nitrite;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvac.persist.impl.model.personal.PojoPrincipal;
import ru.dantalian.copvac.persist.nitrite.hibernate.model.DbPrincipal;

@Singleton
public class NitritePersistPrincipalManager implements PersistPrincipalManager {

	@Inject
	private Nitrite db;

	private ObjectRepository<DbPrincipal> principalRep;

	@Inject
	public void init() {
		principalRep = db.getRepository(DbPrincipal.class);
	}

	@Override
	public Principal getPrincipal(final UUID aId, final String aPasswd) throws PersistException {
		return getPrincipal(aId);
	}

	@Override
	public Principal getPrincipal(final UUID aId) throws PersistException {
		final DbPrincipal dbPrincipal = principalRep.find(ObjectFilters.eq("id", aId)).firstOrDefault();
		return toPrincipal(dbPrincipal);
	}

	@Override
	public Principal createPrincipal(final String aName, final String aDescription) throws PersistException {
		final DbPrincipal dbPrincipal = new DbPrincipal(UUID.randomUUID(), aName, aDescription);
		principalRep.insert(dbPrincipal);
		return toPrincipal(dbPrincipal);
	}

	@Override
	public Principal getPrincipalByName(final String aName) throws PersistException {
		final DbPrincipal dbPrincipal = principalRep.find(ObjectFilters.eq("name", aName)).firstOrDefault();
		return toPrincipal(dbPrincipal);
	}

	@Override
	public void close() {
		this.db.close();
	}

	private Principal toPrincipal(final DbPrincipal aHibPrincipal) {
		if (aHibPrincipal == null) {
			return null;
		}
		return new PojoPrincipal(aHibPrincipal.getId(), aHibPrincipal.getName(), aHibPrincipal.getDescription());
	}

}
