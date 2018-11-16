package ru.dantalian.copvac.persist.nitrite.providers;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer;
import com.orientechnologies.orient.object.db.OrientDBObject;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;

@Singleton
public class DbSessionProvider implements Provider<ODatabaseObject> {

	@Inject
	private OrientDBObject db;

	@Override
	public ODatabaseObject get() {
		final ODatabaseObject session = db.open("user_db", "admin", "admin", OrientDBConfig.defaultConfig());
		final OObjectSerializerContext serializerContext = new OObjectSerializerContext();
		serializerContext.bind(new OObjectSerializer<UUID, String>() {

			@Override
			public Object serializeFieldValue(final Class<?> aIClass, final UUID aIFieldValue) {
				return aIFieldValue.toString();
			}

			@Override
			public Object unserializeFieldValue(final Class<?> aIClass, final String aIFieldValue) {
				return UUID.fromString(aIFieldValue);
			}
		}, session);
		OObjectSerializerHelper.bindSerializerContext(UUID.class, serializerContext);
		session.getEntityManager().registerEntityClasses("ru.dantalian.copvac.persist.nitrite.model");
		session.getMetadata().getSchema().create();
		return session;
	}

}
