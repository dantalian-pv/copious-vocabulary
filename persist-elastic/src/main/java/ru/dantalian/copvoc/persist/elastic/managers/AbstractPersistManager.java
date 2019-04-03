package ru.dantalian.copvoc.persist.elastic.managers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Field;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Id;
import ru.dantalian.copvoc.persist.elastic.model.annotations.SubField;
import ru.dantalian.copvoc.persist.elastic.model.annotations.SubFieldFactory;
import ru.dantalian.copvoc.persist.elastic.model.codecs.CodecException;
import ru.dantalian.copvoc.persist.elastic.model.codecs.DefaultCodec;
import ru.dantalian.copvoc.persist.elastic.model.codecs.FieldCodec;

public abstract class AbstractPersistManager<T> {

	private static final String DEFAULT_TYPE = "_doc";

	private static final DefaultCodec<?, ?> DEFAULT_CODEC = new DefaultCodec<>();

	private static final Set<String> DEFAULT_DATA_TYPES = new HashSet<>(Arrays.asList("keyword", "text",
		  "long", "integer", "short", "byte", "double",
		  "date",
		  "boolean",
		  "binary"));

	private final RestHighLevelClient client;

	private final Class<T> entity;

	private final Set<String> indexCache = new HashSet<>();

	private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

	private final ReadLock readCacheLock = cacheLock.readLock();

	private final WriteLock writeCacheLock = cacheLock.writeLock();

	private final Map<String, FieldCodec<T, ? super Object>> codecMap = new HashMap<>();

	public AbstractPersistManager(final RestHighLevelClient aClient, final Class<T> aEntity) {
		client = aClient;
		entity = aEntity;
	}

	protected abstract String getDefaultIndex();

	protected abstract XContentBuilder getSettings(String aIndex) throws PersistException;

	protected T get(final String aIndex, final String aId) throws PersistException {
		try {
			initIndex(aIndex);
			final GetRequest req = new GetRequest(aIndex, DEFAULT_TYPE, aId);
			final GetResponse response = client.get(req, RequestOptions.DEFAULT);
			final T entiry = map(aId, response.getSourceAsMap());
			if (entiry == null) {
				return null;
			}
			return entiry;
		} catch (final Exception e) {
			throw new PersistException("Failed to get an entity id: " + aId + " from: " + aIndex, e);
		}
	}

	protected void delete(final String aIndex, final String aId) throws PersistException {
		try {
			initIndex(aIndex);
			final DeleteRequest req = new DeleteRequest(aIndex, DEFAULT_TYPE, aId);
			req.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			client.delete(req, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to delete an entity id: " + aId + " from: " + aIndex, e);
		}
	}

	protected void deleteByQuery(final String aIndex, final QueryBuilder aQuery) throws PersistException {
		try {
			initIndex(aIndex);
			final DeleteByQueryRequest req = new DeleteByQueryRequest(aIndex);
			req.setQuery(aQuery);
			req.setRefresh(true);
			client.deleteByQuery(req, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to delete by query: " + aQuery + " from: " + aIndex, e);
		}
	}

	protected void deleteIndex(final String aIndex) throws PersistException {
		try {
			initIndex(aIndex);
			final DeleteIndexRequest req = new DeleteIndexRequest(aIndex);
			client.indices().delete(req, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to delete an index: " + aIndex + " from: " + aIndex, e);
		}
	}

	protected SearchResponse search(final String aIndex, final SearchSourceBuilder aQuery) throws PersistException {
		return search(Collections.singletonList(aIndex), aQuery);
	}

	protected SearchResponse search(final List<String> aIndices, final SearchSourceBuilder aQuery) throws PersistException {
		try {
			for (final String index: aIndices) {
				initIndex(index);
			}
			final SearchRequest searchRequest = new SearchRequest(aIndices.toArray(new String[0]), aQuery);
			final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
			return response;
		} catch (final Exception e) {
			throw new PersistException("Failed to search documents in " + aIndices, e);
		}
	}

	protected void update(final String aIndex, final T aEntity, final boolean aImmediate) throws PersistException {
		try {
			initIndex(aIndex);
			if (aEntity == null) {
				return;
			}
			final Class<?> clazz = aEntity.getClass();
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			if (!Map.class.isAssignableFrom(aEntity.getClass())) {
				builder.startObject();
			}
			{
				addFiledsForClass(aEntity, clazz, builder);
			}
			if (!Map.class.isAssignableFrom(aEntity.getClass())) {
				builder.endObject();
			}
			final String id = getId(aEntity, clazz);
			if (id == null) {
				throw new IllegalArgumentException("No id field found in " + clazz.getName());
			}
			final UpdateRequest updateRequest = new UpdateRequest(aIndex,
					DEFAULT_TYPE,
					id)
	        .doc(builder);
			if (aImmediate) {
				updateRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			}
			client.update(updateRequest, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to create an entity", e);
		}
	}

	protected void updateByScript(final String aIndex, final String aId, final Script aScript,
			final boolean aImmediate) throws PersistException {
		try {
			initIndex(aIndex);
			final UpdateRequest updateRequest = new UpdateRequest(aIndex,
					DEFAULT_TYPE,
					aId)
	        .script(aScript);
			if (aImmediate) {
				updateRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			}
			updateRequest.retryOnConflict(10);
			client.update(updateRequest, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to execute update script", e);
		}
	}

	protected void add(final String aIndex, final T aEntity, final boolean aImmediate) throws PersistException {
		try {
			initIndex(aIndex);
			if (aEntity == null) {
				return;
			}
			final Class<?> clazz = aEntity.getClass();
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			if (!Map.class.isAssignableFrom(aEntity.getClass())) {
				builder.startObject();
			}
			{
				addFiledsForClass(aEntity, clazz, builder);
			}
			if (!Map.class.isAssignableFrom(aEntity.getClass())) {
				builder.endObject();
			}
			final String id = getId(aEntity, clazz);
			if (id == null) {
				throw new IllegalArgumentException("No id field found in " + clazz.getName());
			}
			final IndexRequest indexRequest = new IndexRequest(aIndex,
					DEFAULT_TYPE,
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

	protected T map(final String aId, final Map<String, Object> aSource) throws PersistException {
		try {
			if (aSource == null) {
				return null;
			}
			if (entity == Map.class) {
				return (T) aSource;
			}
			final T instance = entity.newInstance();
			final Class<?> clazz = entity;
			fillFields(instance, clazz, aSource);
			if (aId != null) {
				fillId(instance, entity, aId);
			}
			return instance;
		} catch (final Exception e) {
			throw new PersistException("Failed to create an entity", e);
		}
	}

	protected void fillId(final T aEntiry, final Class<?> aClass, final String aId) throws Exception {
		final java.lang.reflect.Field[] fields = aClass.getDeclaredFields();
		for (final java.lang.reflect.Field field: fields) {
			final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
			final Id idAnnotation = field.getDeclaredAnnotation(Id.class);
			if (idAnnotation != null && fieldAnnotation != null) {
				fillField(aEntiry, aClass, field, fieldAnnotation, Collections.singletonMap(field.getName(), aId));
			}
		}
		Class<?> superclass = null;
		while ((superclass = aClass.getSuperclass()) != null) {
			if (superclass == Object.class) {
				break;
			}
			fillId(aEntiry, superclass, aId);
		}
	}

	protected void fillFields(final T aInstance, final Class<?> aClass, final Map<String, Object> aSource)
			throws Exception {
		final java.lang.reflect.Field[] fields = aClass.getDeclaredFields();
		for (final java.lang.reflect.Field field: fields) {
			final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
			final Id idAnnotation = field.getDeclaredAnnotation(Id.class);
			if (fieldAnnotation != null && idAnnotation == null) {
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

	protected void fillField(final T aInstance, final Class<?> aClass, final java.lang.reflect.Field aField,
			final Field aFieldAnnotation, final Map<String, Object> aSource)
			throws Exception  {
		final String name = aField.getName();
		final String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
		final Method method = aClass.getMethod(setterName, aField.getType());
		final String fieldName = getIndexFieldName(aField, aFieldAnnotation);
		Object data = aSource.get(fieldName);
		final FieldCodec<T, ? super Object> codec = codecMap.getOrDefault(fieldName,
				(FieldCodec<T, ? super Object>) DEFAULT_CODEC);
		data = codec.deserialize(data);
		method.invoke(aInstance, data);
	}

	protected String getId(final T aEntity, final Class<?> aClass) throws Exception {
		if (Map.class.isAssignableFrom(aEntity.getClass())) {
			return (String) ((Map<String, ?>) aEntity).get("id");
		}
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

	protected void addFiledsForClass(final T aEntity, final Class<?> aClass, final XContentBuilder aBuilder)
			throws Exception {
		if (Map.class.isAssignableFrom(aEntity.getClass())) {
			aBuilder.map((Map<String, ?>) aEntity);
			return;
		}
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

	protected void addField(final T aEntity, final Class<?> aClass, final java.lang.reflect.Field aField,
			final Field aFieldAnnotation, final XContentBuilder aBuilder) throws Exception {
		final Id idAnnotation = aField.getAnnotation(Id.class);
		if (idAnnotation != null) {
			// Skip, because this field is ID field
			return;
		}
		final Object data = getFieldData(aEntity, aClass, aField, aFieldAnnotation);
		final String fieldName = getIndexFieldName(aField, aFieldAnnotation);
		aBuilder.field(fieldName, data);
	}

	protected String getIndexFieldName(final java.lang.reflect.Field aField, final Field aFieldAnnotation) {
		return "".equals(aFieldAnnotation.name()) || aFieldAnnotation.name() == null
				? aField.getName() : aFieldAnnotation.name();
	}

	protected Object getFieldData(final T aEntity, final Class<?> aClass, final java.lang.reflect.Field aField,
			final Field aFieldAnnotation)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, CodecException {
		final String name = aField.getName();
		final String getterName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
		Method method = null;
		try {
			method = aClass.getMethod(getterName);
		} catch (final NoSuchMethodException e) {
			final String isName = "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
			method = aClass.getMethod(isName);
		}
		Object data = method.invoke(aEntity);
		final String fieldName = getIndexFieldName(aField, aFieldAnnotation);
		final FieldCodec<T, ? super Object> codec = codecMap.get(fieldName);
		if (codec != null) {
			data = codec.serialize((T) data);
		}
		return data;
	}

	protected void initIndex(final String aIndex) throws PersistException {
		final String index = aIndex == null ? getDefaultIndex() : aIndex;
		try {
			if (isInCache(index)) {
				// Do nothing, because already done
				return;
			}
			final GetIndexRequest exists = new GetIndexRequest();
			exists.indices(index);
			if (client.indices().exists(exists, RequestOptions.DEFAULT)) {
				updateIndexMappings(index);
				saveInCache(index);
				// In case index was created before, but codecs map is needed
				addCodecsForClass(entity);
				return;
			}
			createIndex(index);
			saveInCache(index);
		} catch (final IOException | InstantiationException | IllegalAccessException e) {
			throw new PersistException("Failed to create index: " + index, e);
		}
	}

	private void updateIndexMappings(final String aIndex)
			throws InstantiationException, IllegalAccessException, IOException {
		final PutMappingRequest putMappings = new PutMappingRequest(aIndex);
		putMappings.type(DEFAULT_TYPE);
		final XContentBuilder mappings = createMappings();
		if (mappings == null) {
			return;
		}
		putMappings.source(mappings);
		client.indices().putMapping(putMappings, RequestOptions.DEFAULT);
	}

	private void createIndex(final String aIndex)
			throws IOException, InstantiationException, IllegalAccessException, PersistException {
		final CreateIndexRequest createIndex = new CreateIndexRequest(aIndex);
		final XContentBuilder mappings = createMappings();

		final XContentBuilder settings = getSettings(aIndex);
		if (mappings != null) {
			createIndex.mapping(DEFAULT_TYPE, mappings);
		}
		if (settings != null) {
			createIndex.settings(settings);
		}
		client.indices().create(createIndex, RequestOptions.DEFAULT);
	}

	private XContentBuilder createMappings() throws IOException, InstantiationException, IllegalAccessException {
		final XContentBuilder mappings = XContentFactory.jsonBuilder();
		mappings.startObject();
		{
			mappings.startObject(DEFAULT_TYPE);
		  {
		  	mappings.startObject("properties");
		    {
		    	addMappingForClass(mappings, entity, false);
		    }
		    mappings.endObject();
		    // dynamic templates
		    mappings.startArray("dynamic_templates");
		    {
		    	addMappingForClass(mappings, entity, true);
		    }
		    mappings.endArray();
		  }
		  mappings.endObject();
		}
		mappings.endObject();
		return mappings;
	}

	protected void addCodecsForClass(final Class<?> aEntity)
			throws InstantiationException, IllegalAccessException {
		if (aEntity == Map.class) {
			return;
		}
		final java.lang.reflect.Field[] fields = aEntity.getDeclaredFields();
		final Method[] methods = aEntity.getDeclaredMethods();
		for (final java.lang.reflect.Field field: fields) {
			final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
			if (fieldAnnotation != null) {
				addFieldCodec(field, fieldAnnotation);
			}
		}
		for (final Method method: methods) {
			final Field fieldAnnotation = method.getDeclaredAnnotation(Field.class);
			if (fieldAnnotation != null) {
				addMethodCodec(method, fieldAnnotation);
			}
		}
		Class<?> superclass = null;
		while ((superclass = aEntity.getSuperclass()) != null) {
			if (superclass.equals(Object.class)) {
				break;
			}
			addCodecsForClass(superclass);
		}
	}

	private void addMethodCodec(final Method aMethod, final Field aFieldAnnotation) throws InstantiationException, IllegalAccessException  {
		String name = "".equals(aFieldAnnotation.name()) || aFieldAnnotation.name() == null
				? aMethod.getName() : aFieldAnnotation.name();
		if (!name.startsWith("get")) {
			return;
		}
		name = name.substring(2).toLowerCase();
		addCodec(name, aFieldAnnotation.codec());
	}

	private void addFieldCodec(final java.lang.reflect.Field aField, final Field aFieldAnnotation) throws InstantiationException, IllegalAccessException  {
		final String name = getIndexFieldName(aField, aFieldAnnotation);
		addCodec(name, aFieldAnnotation.codec());
	}

	private void addCodec(final String aName, final Class<? extends FieldCodec> aCodec) throws InstantiationException, IllegalAccessException {
		if (aCodec != null && !aCodec.isAssignableFrom(DefaultCodec.class)) {
			codecMap.put(aName, aCodec.newInstance());
		}
	}

	protected void addMappingForClass(final XContentBuilder aMappings, final Class<?> aEntity, final boolean aDynamic)
			throws IOException, InstantiationException, IllegalAccessException {
		if (aEntity == Map.class) {
			if (!aDynamic) {
				return;
			}
			for (final String type: new String[] {"keyword", "text",
					"long", "integer", "short", "byte", "double",
					"date",
					"boolean",
					"binary",
					}) {
				addMapping("map", "object",
						new SubField[] {SubFieldFactory.create("*_" + type, null, type, true, false, null)},
						true, null, aMappings, true);
			}
		} else {
			final java.lang.reflect.Field[] fields = aEntity.getDeclaredFields();
			final Method[] methods = aEntity.getDeclaredMethods();
			for (final java.lang.reflect.Field field: fields) {
				final Field fieldAnnotation = field.getDeclaredAnnotation(Field.class);
				if (fieldAnnotation != null) {
					addFieldIndex(field, fieldAnnotation, aMappings, aDynamic);
				}
			}
			for (final Method method: methods) {
				final Field fieldAnnotation = method.getDeclaredAnnotation(Field.class);
				if (fieldAnnotation != null) {
					addMethodIndex(method, fieldAnnotation, aMappings, aDynamic);
				}
			}
			Class<?> superclass = null;
			while ((superclass = aEntity.getSuperclass()) != null) {
				if (superclass.equals(Object.class)) {
					break;
				}
				addMappingForClass(aMappings, superclass, aDynamic);
			}
		}
	}

	protected void addMethodIndex(final Method aMethod, final Field aMethodAnnotation,
			final XContentBuilder aMappings, final boolean aDynamic)
			throws InstantiationException, IllegalAccessException, IOException {
		String name = "".equals(aMethodAnnotation.name()) || aMethodAnnotation.name() == null
				? aMethod.getName() : aMethodAnnotation.name();
		if (!name.startsWith("get")) {
			return;
		}
		name = name.substring(2).toLowerCase();
		final String type = getMethodType(aMethod, aMethodAnnotation);
		addMapping(name, type, aMethodAnnotation.subtype(), aMethodAnnotation.index(), aMethodAnnotation.codec(), aMappings, aDynamic);
	}

	private String getMethodType(final Method aMethod, final Field aFieldAnnotation) {
		final Class<?> methodType = aMethod.getReturnType();
		return getDataType(aFieldAnnotation.type(), methodType);
	}

	protected void addFieldIndex(final java.lang.reflect.Field aField, final Field aFieldAnnotation,
			final XContentBuilder aMappings, final boolean aDynamic) throws IOException, InstantiationException, IllegalAccessException {
		final String name = getIndexFieldName(aField, aFieldAnnotation);
		final String type = getFieldType(aField, aFieldAnnotation);
		addMapping(name, type, aFieldAnnotation.subtype(), aFieldAnnotation.index(), aFieldAnnotation.codec(), aMappings, aDynamic);
	}

	protected String getFieldType(final java.lang.reflect.Field aField, final Field aFieldAnnotation) {
		final Class<?> fieldType = aField.getType();
		return getDataType(aFieldAnnotation.type(), fieldType);
	}

	private String getDataType(final String aAnotationType, final Class<?> fieldType) {
		final String type  = aAnotationType;
		final String typeName = fieldType.getSimpleName().toLowerCase();
		if (type == null || type.isEmpty()) {
			if (fieldType.isPrimitive()) {
				return typeName;
			} else if (DEFAULT_DATA_TYPES.contains(typeName) && fieldType.getName().startsWith("java.lang")) {
				return typeName;
			} else if (fieldType.isArray()) {
				return getDataType(aAnotationType, fieldType.getComponentType());
			} else if (Collection.class.isAssignableFrom(fieldType)) {
				final ParameterizedType paramType = (ParameterizedType) fieldType.getGenericSuperclass();
        final Class<?> genericColletionClass = (Class<?>) paramType.getActualTypeArguments()[0];
        return getDataType(aAnotationType, genericColletionClass);
			} else {
				return "keyword";
			}
		} else {
			return type;
		}
	}

	protected void addMapping(final String aName, final String aType, final SubField[] aSubfields, final boolean aIndex,
			final Class<? extends FieldCodec> aCodec, final XContentBuilder aMappings, final boolean aDynamic)
					throws IOException, InstantiationException, IllegalAccessException {
		if ("object".equals(aType) && aDynamic && aSubfields != null) {
			for (final SubField subField: aSubfields) {
				aMappings.startObject();
				{
					aMappings.startObject(aName + "_dynamic");
					{
						aMappings.field("path_match", subField.path_match());
						if (!subField.path_unmatch().isEmpty()) {
							aMappings.field("path_unmatch", subField.path_unmatch());
						}
						aMappings.startObject("mapping");
						{
							aMappings.field("type", subField.type());
							aMappings.field("index", subField.index());
						}
						aMappings.endObject();
					}
					aMappings.endObject();
				}
				aMappings.endObject();
			}
		} else if (!"object".equals(aType) && !aDynamic) {
			aMappings.startObject(aName);
				aMappings.field("type", aType);
				aMappings.field("index", aIndex);
			aMappings.endObject();
		}
		addCodec(aName, aCodec);
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
