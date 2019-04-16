package ru.dantalian.copvoc.web.controllers.rest.model;

import java.util.List;

public class DtoQueryResult<T> {

	private List<T> items;

	private long total;

	private int from;

	private int limit;

	public DtoQueryResult() {
	}

	public DtoQueryResult(final List<T> aItems, final long aTotal, final int aFrom, final int aLimit) {
		items = aItems;
		total = aTotal;
		from = aFrom;
		limit = aLimit;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(final List<T> aItems) {
		items = aItems;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(final long aTotal) {
		total = aTotal;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(final int aFrom) {
		from = aFrom;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(final int aLimit) {
		limit = aLimit;
	}

}
