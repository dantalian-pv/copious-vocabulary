package ru.dantalian.copvac.persist.orientdb.managers;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.object.db.OrientDBObject;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvac.persist.impl.model.personal.PojoPrincipal;
import ru.dantalian.copvac.persist.orientdb.model.DbPrincipal;

@Singleton
public class OrientPersistPrincipalManager implements PersistPrincipalManager {

	@Inject
	private ODatabaseObject session;

	@Inject
	private OrientDBObject db;

	@Override
	public Principal getPrincipal(final UUID aId, final String aPasswd) throws PersistException {
		return getPrincipal(aId);
	}

	@Override
	public Principal getPrincipal(final UUID aId) throws PersistException {
		try {
			final OResultSet res = session.query("select * from DbPrincipal where id = ?", aId);
			if(res.hasNext()) {
				return toPrincipal((DbPrincipal) session.getUserObjectByRecord(res.next().toElement(), null));
			}
			return null;
		} catch (final OCommandSQLParsingException | OCommandExecutionException e) {
			throw new PersistException("Failed to get a principal", e);
		}
	}

	@Override
	public Principal createPrincipal(final String aName, final String aDescription) throws PersistException {
		try {
			final DbPrincipal dbPrincipal = new DbPrincipal(UUID.randomUUID(), aName, aDescription);
			session.save(dbPrincipal);
			return toPrincipal(dbPrincipal);
		} catch (final OCommandSQLParsingException | OCommandExecutionException e) {
			throw new PersistException("Failed to create a principal", e);
		}
	}

	@Override
	public Principal getPrincipalByName(final String aName) throws PersistException {
		try {
			final OResultSet res = session.query("select * from DbPrincipal where name = ?", aName);
			if(res.hasNext()) {
				return toPrincipal((DbPrincipal) session.getUserObjectByRecord(res.next().toElement(), null));
			}
			return null;
		} catch (final OCommandSQLParsingException | OCommandExecutionException e) {
			throw new PersistException("Failed to get a principal by name", e);
		}
	}

	@Override
	public void close() {
		this.session.close();
		this.db.close();
	}

	private Principal toPrincipal(final DbPrincipal aHibPrincipal) {
		if (aHibPrincipal == null) {
			return null;
		}
		return new PojoPrincipal(aHibPrincipal.getId(), aHibPrincipal.getName(), aHibPrincipal.getDescription());
	}

}
