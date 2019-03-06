package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.Map;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCacheManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;

@Service
public class ElasticPersistCacheManager extends AbstractPersistManager<Map<String, Object>>
	implements PersistCacheManager {

	private static final String DEFAULT_INDEX = "cache";

	private final ElasticSettings settings;

	@Autowired
	public ElasticPersistCacheManager(final RestHighLevelClient aClient, final ElasticSettings aSettings) {
		super(aClient, (Class) Map.class);
		settings = aSettings;
	}

	@Override
	protected String getDefaultIndex() {
		return DEFAULT_INDEX;
	}

	@Override
	protected XContentBuilder getSettings(final String aIndex) throws PersistException {
		return settings.getDefaultSettings();
	}

	@Override
	public void save(final String aHashCode, final Map<String, Object> aMap) throws PersistException {
		add(getDefaultIndex(), aMap, true);
	}

	@Override
	public Map<String, Object> load(final String aHashCode) throws PersistException {
		return get(getDefaultIndex(), aHashCode);
	}

}
