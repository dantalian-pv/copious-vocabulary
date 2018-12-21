package ru.dantalian.copvoc.persist.elastic.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ElasticSettings {

	@Value("${elatic.hosts}")
	private List<String> hosts;

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

}
