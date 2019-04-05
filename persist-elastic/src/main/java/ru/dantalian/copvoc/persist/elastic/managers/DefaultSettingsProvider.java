package ru.dantalian.copvoc.persist.elastic.managers;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;
import ru.dantalian.copvoc.persist.elastic.orm.IndexSettingsProvider;

@Service
public class DefaultSettingsProvider implements IndexSettingsProvider {

	@Autowired
	private ElasticSettings settings;

	@Override
	public XContentBuilder getSettings(final String aIndex) throws PersistException {
		return settings.getDefaultSettings();
	}

}
