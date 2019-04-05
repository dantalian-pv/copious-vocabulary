package ru.dantalian.copvoc.persist.elastic.orm;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticORMFactory {

	@Autowired
	private RestHighLevelClient client;

	public <T> ElasticORM<T> newElasticORM(final Class<T> aEntity, final IndexSettingsProvider aSettingsProvider) {
		return new ElasticORM<>(client, aEntity, aSettingsProvider);
	}

}
