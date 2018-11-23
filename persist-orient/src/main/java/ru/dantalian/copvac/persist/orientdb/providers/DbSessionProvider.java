package ru.dantalian.copvac.persist.orientdb.providers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer;
import com.orientechnologies.orient.object.db.OrientDBObject;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;

import ru.dantalian.copvac.persist.orientdb.api.Index;

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
		session.getEntityManager().registerEntityClasses("ru.dantalian.copvac.persist.orientdb.model");
		session.getMetadata().getSchema().synchronizeSchema();
		createIndices(session);
		//session.setConflictStrategy(new OAutoMergeRecordConflictStrategy());
		return session;
	}

	private void createIndices(final ODatabaseObject session) {
		final Collection<Class<?>> registeredEntities = session.getEntityManager().getRegisteredEntities();
		for (final Class<?> entity: registeredEntities) {
			final Index[] indices = entity.getAnnotationsByType(Index.class);
			for (final Index index: indices) {
				final OClass clazz = session.getClass(entity.getSimpleName());

				final Set<String> existingIndices = clazz.getIndexes()
						.stream()
						.map(aIndex -> aIndex.getName())
						.collect(Collectors.toSet());
				if (!existingIndices.contains(index.name())) {
					for (final String column: index.columnList()) {
						final OProperty property = clazz.getProperty(column);
						if (property == null) {
							clazz.createProperty(column, getOType(column, entity));
						}
					}
					clazz.createIndex(index.name(), index.indexType(), index.columnList());
				}
			}
		}
	}

	private OType getOType(final String aColumn, final Class<?> aEntity) {
		try {
			final Field field = aEntity.getDeclaredField(aColumn);
			final Class<?> clazz = field.getType();
			if (String.class.isAssignableFrom(clazz)) {
				return OType.STRING;
			} else if (Boolean.class.isAssignableFrom(clazz)) {
				return OType.BOOLEAN;
			} else if (Double.class.isAssignableFrom(clazz)) {
				return OType.DOUBLE;
			} else if (Integer.class.isAssignableFrom(clazz)) {
				return OType.INTEGER;
			} else if (Short.class.isAssignableFrom(clazz)) {
				return OType.SHORT;
			} else if (Byte.class.isAssignableFrom(clazz)) {
				return OType.BYTE;
			} else {
				return OType.ANY;
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException("Failed to get filed " + aColumn + " from " + aEntity.getName(), e);
		}
	}

}
