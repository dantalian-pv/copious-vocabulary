package ru.dantalian.copvac.persist.sqlite.managers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistPrincipalManager;
import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvac.persist.impl.model.personal.PojoPrincipal;
import ru.dantalian.copvac.persist.sqlite.model.DbPrincipal;
import ru.dantalian.copvac.persist.sqlite.model.mappers.DbPrincipalMapper;

@Service
public class SqlitePersistPrincipalManager implements PersistPrincipalManager {

	@Autowired
	private JdbcTemplate db;

	@Autowired
	private DbPrincipalMapper mapper;

	@Override
	public Principal createPrincipal(final String aName, final String aDescription) throws PersistException {
		try {
			db.update("REPLACE INTO principal (name, description) values " +
					"(?, ?)", aName, aDescription);
			return toPrincipal(new DbPrincipal(aName, aDescription));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to create a principal", e);
		}
	}

	@Override
	public Principal getPrincipalByName(final String aName) throws PersistException {
		try {
			final List<DbPrincipal> list = db.query("select * from principal where name = ?",
					new Object[] {
							aName
					},
					mapper);
			return toPrincipal(CollectionUtils.lastElement(list));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get a principal by name", e);
		}
	}

	@Override
	public void close() {
	}

	private Principal toPrincipal(final DbPrincipal aHibPrincipal) {
		if (aHibPrincipal == null) {
			return null;
		}
		return new PojoPrincipal(aHibPrincipal.getName(), aHibPrincipal.getDescription());
	}

}
