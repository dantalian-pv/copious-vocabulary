package ru.dantalian.copvoc.persist.api.query;

import java.util.List;

public interface QueryResult<T> {

	List<T> getItems();

	long getTotal();

}
