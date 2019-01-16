package ru.dantalian.copvoc.suggester.api;

public interface Builder<T> {

	T build() throws SuggestException;

}
