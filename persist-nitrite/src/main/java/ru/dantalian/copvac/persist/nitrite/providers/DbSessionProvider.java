package ru.dantalian.copvac.persist.nitrite.providers;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.object.db.OrientDBObject;

@Singleton
public class DbSessionProvider implements Provider<ODatabaseObject> {

	@Inject
	private OrientDBObject db;

	@Override
	public ODatabaseObject get() {
		final ODatabaseObject session = db.open("user_db", "root", "root", OrientDBConfig.defaultConfig());
		session.getEntityManager().registerEntityClasses("ru.dantalian.copvac.persist.nitrite.model");
		session.getMetadata().getSchema().create();
		return session;
	}

}
