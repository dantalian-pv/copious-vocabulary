package ru.dantalian.copvoc.persist.elastic.providers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;

@Component
public class ClientProvider extends AbstractFactoryBean<RestHighLevelClient> {

	@Autowired
	private ElasticSettings settings;

	@Override
	public Class<?> getObjectType() {
		return RestHighLevelClient.class;
	}

	@Override
	protected RestHighLevelClient createInstance() throws Exception {
		final List<HttpHost> hosts = settings.getElasticHosts()
				.stream()
				.map(aItem -> new HttpHost(aItem.getHost(),
						aItem.getPort() == null ? -1 : aItem.getPort(),
								aItem.getScheme()))
				.collect(Collectors.toList());
		final RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(hosts.toArray(new HttpHost[0])));
		return client;
	}

}
