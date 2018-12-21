package ru.dantalian.copvoc.persist.elastic.common;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;

public interface IndexHandler {

	void initIndex() throws ElasticsearchException, IOException;

	void commit() throws ElasticsearchException, IOException;

}
