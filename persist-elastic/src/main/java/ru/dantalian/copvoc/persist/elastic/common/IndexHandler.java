package ru.dantalian.copvoc.persist.elastic.common;

import org.elasticsearch.common.xcontent.XContentBuilder;

import ru.dantalian.copvoc.persist.api.PersistException;

public interface IndexHandler {

	void initIndex(String aIndex) throws PersistException;

	XContentBuilder getSettings(String aIndex) throws PersistException;

}
