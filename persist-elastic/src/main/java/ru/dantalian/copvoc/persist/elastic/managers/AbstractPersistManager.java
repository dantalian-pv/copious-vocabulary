package ru.dantalian.copvoc.persist.elastic.managers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.elastic.common.IndexHandler;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Field;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Id;
import ru.dantalian.copvoc.persist.elastic.model.codecs.DefaultCodec;
import ru.dantalian.copvoc.persist.elastic.model.codecs.FieldCodec;

public abstract class AbstractPersistManager<T> implements IndexHandler {

	private final RestHighLevelClient client;

	private final Class<T> entity;

	private final Set<String> indexCache = new HashSet<>();

	private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

	private final ReadLock readCacheLock = cacheLock.readLock();

	private final WriteLock writeCacheLock = cacheLock.writeLock();

	private final Map<String, FieldCodec<T, ?>> codecMap = new HashMap<>();

	public AbstractPersistManager(final RestHighLevelClient aClient, final Class<T> aEntity) {
		client = aClient;
		entity = aEntity;
	}

	public abstract String getDefaultIndex();

	protected void add(final String aIndex, final T aEntity, final boolean aImmediate) throws PersistException {
		try {
			if (aEntity == null) {
				return;
			}
			final Class<?> clazz = aEntity.getClass();
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
				addFiledsForClass(aEntity, clazz, builder);
			}
			builder.endObject();
			final String id = getId(aEntity, clazz);
			if (id == null) {
				throw new IllegalArgumentException("No id field found in " + clazz.getName());
			}
			final IndexRequest indexRequest = new IndexRequest(aIndex,
					"_doc",
					id)
	        .source(builder);
			if (aImmediate) {
				indexRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			}
			client.index(indexRequest, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to create an entity", e);
		}
	}

	protected T map(final Map<String, Object> aSource) throws PersistException {
		try {
			if (aSource == null) {
				return null;
			}
			final T instance = entity.newInstance();
			final Class<?> clazz = entity;
			fillFields(instance, clazz, aSource);
			return instance;
		} catch (final Exception e) {
			throw new PersistException("Failed to create an entity", e);
		}
	}

	private void fillFields(final T aInstance, final Class<?> aClass, final Map<String, Object> aSource)
			throws Exception {
		final java.lang.reflect.Field[] fields = aClass.getDeclaredFields();
		for (final java.lang.reflect.Field field: fields) {
			final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
			if (fieldAnnotation != null) {
				fillField(aInstance, aClass, field, fieldAnnotation, aSource);
			}
		}
		Class<?> superclass = null;
		while ((superclass = aClass.getSuperclass()) != null) {
			if (superclass == Object.class) {
				break;
			}
			fillFields(aInstance, superclass, aSource);
		}
	}

	private void fillField(final T aInstance, final Class<?> aClass, final java.lang.reflect.Field aField,
			final Field aFieldAnnotation, final Map<String, Object> aSource)
			throws Exception  {
		final String name = aField.getName();
		final String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
		final Method method = aClass.getMethod(setterName, aField.getType());
		final String fieldName = getIndexFieldName(aField, aFieldAnnotation);
		Object data = aSource.get(fieldName);
		final FieldCodec codec = codecMap.get(fieldName);
		if (codec != null) {
			data = codec.deserialize(data);
		}
		method.invoke(aInstance, data);
	}

	private String getId(final T aEntity, final Class<?> aClass) throws Exception {
		final java.lang.reflect.Field[] fields = aClass.getDeclaredFields();
		for (final java.lang.reflect.Field field: fields) {
			final Id idAnnotation = field.getDeclaredAnnotation(Id.class);
			final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
			if (idAnnotation != null) {
				return (String) getFieldData(aEntity, aClass, field, fieldAnnotation);
			}
		}
		Class<?> superclass = null;
		while ((superclass = aClass.getSuperclass()) != null) {
			if (superclass == Object.class) {
				break;
			}
			final String id = getId(aEntity, superclass);
			if (id != null) {
				return id;
			}
		}
		return null;
	}

	private void addFiledsForClass(final T aEntity, final Class<?> aClass, final XContentBuilder aBuilder)
			throws Exception {
		final java.lang.reflect.Field[] fields = aClass.getDeclaredFields();
		for (final java.lang.reflect.Field field: fields) {
			final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
			if (fieldAnnotation != null) {
				addField(aEntity, aClass, field, fieldAnnotation, aBuilder);
			}
		}
		Class<?> superclass = null;
		while ((superclass = aClass.getSuperclass()) != null) {
			if (superclass == Object.class) {
				break;
			}
			addFiledsForClass(aEntity, superclass, aBuilder);
		}
	}

	private void addField(final T aEntity, final Class<?> aClass, final java.lang.reflect.Field aField,
			final Field aFieldAnnotation, final XContentBuilder aBuilder) throws Exception {
		final Object data = getFieldData(aEntity, aClass, aField, aFieldAnnotation);
		final String fieldName = getIndexFieldName(aField, aFieldAnnotation);
		aBuilder.field(fieldName, data);
	}

	private String getIndexFieldName(final java.lang.reflect.Field aField, final Field aFieldAnnotation) {
		return "".equals(aFieldAnnotation.name()) || aFieldAnnotation.name() == null
				? aField.getName() : aFieldAnnotation.name();
	}

	private Object getFieldData(final T aEntity, final Class<?> aClass, final java.lang.reflect.Field aField,
			final Field aFieldAnnotation)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final String name = aField.getName();
		final String getterName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
		final Method method = aClass.getMethod(getterName);
		Object data = method.invoke(aEntity);
		final String fieldName = getIndexFieldName(aField, aFieldAnnotation);
		final FieldCodec codec = codecMap.get(fieldName);
		if (codec != null) {
			data = codec.serialize(data);
		}
		return data;
	}

	@Override
	public void initIndex(final String aIndex) throws PersistException {
		final String index = aIndex == null ? getDefaultIndex() : aIndex;
		try {
			if (isInCache(index)) {
				// Do nothing, because already done
				return;
			}
			final GetIndexRequest exists = new GetIndexRequest();
			exists.indices(index);
			if (client.indices().exists(exists, RequestOptions.DEFAULT)) {
				saveInCache(index);
				return;
			}
			final CreateIndexRequest createIndex = new CreateIndexRequest(index);
			final XContentBuilder mappings = XContentFactory.jsonBuilder();
			mappings.startObject();
			{
				mappings.startObject("_doc");
		    {
		    	mappings.startObject("properties");
	        {
	        	addMappingForClass(mappings, entity);
	        }
	        mappings.endObject();
		    }
		    mappings.endObject();
			}
			mappings.endObject();

			final XContentBuilder settings = getSettings(index);
			if (mappings != null) {
				createIndex.mapping("_doc", mappings);
			}
			if (settings != null) {
				createIndex.settings(settings);
			}
			client.indices().create(createIndex, RequestOptions.DEFAULT);
			saveInCache(index);
		} catch (final IOException | InstantiationException | IllegalAccessException e) {
			throw new PersistException("Failed to create index: " + index, e);
		}
	}

	protected void addMappingForClass(final XContentBuilder mappings, final Class<?> aEntity)
			throws IOException, InstantiationException, IllegalAccessException {
		final java.lang.reflect.Field[] fields = aEntity.getDeclaredFields();
		final Method[] methods = aEntity.getDeclaredMethods();
		for (final java.lang.reflect.Field field: fields) {
			final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
			if (fieldAnnotation != null) {
				addFieldIndex(field, fieldAnnotation, mappings);
			}
		}
		for (final Method method: methods) {
			final Field fieldAnnotation = method.getDeclaredAnnotation(Field.class);
			if (fieldAnnotation != null) {
				addMethodIndex(method, fieldAnnotation, mappings);
			}
		}
		Class<?> superclass = null;
		while ((superclass = aEntity.getSuperclass()) != null) {
			if (superclass.equals(Object.class)) {
				break;
			}
			addMappingForClass(mappings, superclass);
		}
	}

	protected void addMethodIndex(final Method aMethod, final Field aFieldAnnotation, final XContentBuilder aMappings)
			throws InstantiationException, IllegalAccessException, IOException {
		String name = "".equals(aFieldAnnotation.name()) || aFieldAnnotation.name() == null
				? aMethod.getName() : aFieldAnnotation.name();
		if (!name.startsWith("get")) {
			return;
		}
		name = name.substring(2).toLowerCase();
		addMapping(name, aFieldAnnotation.type(), aFieldAnnotation.index(), aFieldAnnotation.codec(), aMappings);
	}

	protected void addFieldIndex(final java.lang.reflect.Field aField, final Field aFieldAnnotation,
			final XContentBuilder aMappings) throws IOException, InstantiationException, IllegalAccessException {
		final String name = getIndexFieldName(aField, aFieldAnnotation);
		addMapping(name, aFieldAnnotation.type(), aFieldAnnotation.index(), aFieldAnnotation.codec(), aMappings);
	}

	protected void addMapping(final String aName, final String aType, final boolean aIndex,
			final Class<? extends FieldCodec> aCodec, final XContentBuilder aMappings)
					throws IOException, InstantiationException, IllegalAccessException {
		aMappings.startObject(aName);
			aMappings.field("type", aType);
			aMappings.field("index", aIndex);
		aMappings.endObject();
		if (aCodec != null && !aCodec.isAssignableFrom(DefaultCodec.class)) {
			codecMap.put(aName, aCodec.newInstance());
		}
	}

	protected boolean isInCache(final String aIndex) {
		try {
			readCacheLock.lock();
			return indexCache.contains(aIndex);
		} finally {
			readCacheLock.unlock();
		}
	}

	protected void saveInCache(final String aIndex) {
		try {
			writeCacheLock.lock();
			indexCache.add(aIndex);
		} finally {
			writeCacheLock.unlock();
		}
	}

}
