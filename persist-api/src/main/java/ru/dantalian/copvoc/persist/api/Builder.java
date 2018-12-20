package ru.dantalian.copvoc.persist.api;

public interface Builder<T> {

	T build() throws PersistException;

}
