package ru.dantalian.copvoc.persist.api.config;

public interface AppConfigMap {

	<T> T get(String aProperty);

}
