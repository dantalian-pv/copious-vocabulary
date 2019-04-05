package ru.dantalian.copvoc.persist.elastic.orm;

import org.elasticsearch.common.xcontent.XContentBuilder;

import ru.dantalian.copvoc.persist.api.PersistException;

public interface IndexSettingsProvider {

	XContentBuilder getSettings(String aIndex) throws PersistException;

}
