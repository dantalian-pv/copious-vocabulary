package ru.dantalian.copvoc.persist.elastic.common;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;

public class IndexTransaction implements AutoCloseable {

	private final IndexHandler handler;
	private final boolean writeOpration;

	public static IndexTransaction newInstance(final IndexHandler aHandler, final boolean aWriteOpration)
			throws ElasticsearchException, IOException {
		aHandler.initIndex();
		return new IndexTransaction(aHandler, aWriteOpration);
	}

	private IndexTransaction(final IndexHandler aHandler, final boolean aWriteOpration) {
		handler = aHandler;
		writeOpration = aWriteOpration;
	}

	@Override
	public void close() throws Exception {
		if (writeOpration) {
			handler.commit();
		}
	}

}
