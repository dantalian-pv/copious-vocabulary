package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCacheManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORM;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORMFactory;

@Service
public class ElasticPersistCacheManager implements PersistCacheManager {

	private static final String DEFAULT_INDEX = "cache";

	@Autowired
	private DefaultSettingsProvider settingsProvider;

	@Autowired
	private ElasticORMFactory ormFactory;

	private ElasticORM<Map<String, Object>> orm;

	@PostConstruct
	public void init() {
		orm = ormFactory.newElasticORM((Class) Map.class, settingsProvider);
	}

	@Override
	public void save(final Map<String, Object> aMap) throws PersistException {
		orm.add(DEFAULT_INDEX, aMap, true);
	}

	@Override
	public Map<String, Object> load(final String aHashCode) throws PersistException {
		return orm.get(DEFAULT_INDEX, aHashCode);
	}

}
