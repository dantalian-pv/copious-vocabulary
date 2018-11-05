package ru.dantalian.copvac.persist.nitrite.managers;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.exceptions.NitriteException;
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
		try {
			final DbPrincipal dbPrincipal = principalRep.find(ObjectFilters.eq("id", aId)).firstOrDefault();
			return toPrincipal(dbPrincipal);
		} catch (final NitriteException e) {
			throw new PersistException("Failed to get a principal", e);
		}
	}

	@Override
	public Principal createPrincipal(final String aName, final String aDescription) throws PersistException {
		try {
			final DbPrincipal dbPrincipal = new DbPrincipal(UUID.randomUUID(), aName, aDescription);
			principalRep.insert(dbPrincipal);
			return toPrincipal(dbPrincipal);
		} catch (final NitriteException e) {
			throw new PersistException("Failed to create a principal", e);
		}
	}

	@Override
	public Principal getPrincipalByName(final String aName) throws PersistException {
		try {
			final DbPrincipal dbPrincipal = principalRep.find(ObjectFilters.eq("name", aName)).firstOrDefault();
			return toPrincipal(dbPrincipal);
		} catch (final NitriteException e) {
			throw new PersistException("Failed to get a principal by name", e);
		}
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
