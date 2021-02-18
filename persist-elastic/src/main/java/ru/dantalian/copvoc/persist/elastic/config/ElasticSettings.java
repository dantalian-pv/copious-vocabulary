package ru.dantalian.copvoc.persist.elastic.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.config.AppConfigMap;

@Configuration
public class ElasticSettings {

	@Value("${elastic.hosts}")
	private List<String> hosts;

	@Autowired
	private AppConfigMap configMap;

	public List<ElasticHost> getElasticHosts() {
		if (hosts == null || hosts.isEmpty()) {
			return Collections.emptyList();
		}
		final List<ElasticHost> list = new LinkedList<>();
		for (final String host: hosts) {
			final String[] parts = host.split(":");
			list.add(new ElasticHost(parts[0],
				parts.length > 1 ? Integer.parseInt(parts[1]) : null,
				parts.length > 2 ? parts[2] : null));
		}
		return list;
	}

	public List<String> getHosts() {
		return hosts;
	}

	public void setHosts(final List<String> aHosts) {
		hosts = aHosts;
	}

	public Map<String, Object> getIndexSettings() {
		return configMap.get("elastic.index");
	}

	public XContentBuilder getDefaultSettings() throws PersistException {
		try {
			final XContentBuilder settings = XContentFactory.jsonBuilder();
			settings.startObject();
			{
				settings.startObject("index");
				{
					final Map<String, Object> def = getIndexSettings();
					for (final Entry<String, Object> entry: def.entrySet()) {
						settings.field(entry.getKey(), entry.getValue());
					}
				}
				settings.endObject();
			}
			settings.endObject();
			return settings;
		} catch (final Exception e) {
			throw new PersistException("Failed to build default settings", e);
		}
	}

}
