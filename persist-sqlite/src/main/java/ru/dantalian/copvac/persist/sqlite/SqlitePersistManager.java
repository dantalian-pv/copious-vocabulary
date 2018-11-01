package ru.dantalian.copvac.persist.sqlite;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistManager;
import ru.dantalian.copvac.persist.api.model.personal.Principal;
import ru.dantalian.copvac.persist.impl.model.personal.PojoPrincipal;
import ru.dantalian.copvac.persist.sqlite.hibernate.model.HibPrincipal;

@Singleton
public class SqlitePersistManager implements PersistManager {

	private static final Logger logger = LoggerFactory.getLogger(SqlitePersistManager.class);

	@Inject
	private EntityManager entityManager;

	@Override
	public Principal getPrincipal(final String aId, final String aPasswd) throws PersistException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Principal getPrincipal(final String aId) throws PersistException {
		final HibPrincipal hibPrincipal = entityManager.find(HibPrincipal.class, aId);
		return toPrincipal(hibPrincipal);
	}

	@Override
	public Principal createPrincipal(final String aName, final String aDescription) throws PersistException {
		final HibPrincipal hibPrincipal = new HibPrincipal(UUID.randomUUID().toString(), aName, aDescription);
		entityManager.persist(hibPrincipal);
		return toPrincipal(hibPrincipal);
	}

	@Override
	public Principal getPrincipalByName(final String aName) throws PersistException {
		final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		final CriteriaQuery<HibPrincipal> query = builder.createQuery(HibPrincipal.class);
		final Root<HibPrincipal> root = query.from(HibPrincipal.class);
		query.select(root).where(builder.equal(root.get("name"), aName));
		final List<HibPrincipal> list = entityManager.createQuery(query).getResultList();
		if (list.isEmpty()) {
			return null;
		}
		return toPrincipal(list.get(0));
	}

	@Override
	public void close() {
		try {
			entityManager.close();
		} catch (final HibernateException e) {
			logger.error("Failed to close EntityManger", e);
		}
	}

	private Principal toPrincipal(final HibPrincipal aHibPrincipal) {
		if (aHibPrincipal == null) {
			return null;
		}
		return new PojoPrincipal(aHibPrincipal.getId(), aHibPrincipal.getName(), aHibPrincipal.getDescription());
	}

}
