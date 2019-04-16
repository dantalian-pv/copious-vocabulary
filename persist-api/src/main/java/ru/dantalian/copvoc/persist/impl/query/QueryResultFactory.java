package ru.dantalian.copvoc.persist.impl.query;

import java.util.List;

import ru.dantalian.copvoc.persist.api.query.QueryResult;

public final class QueryResultFactory {

	private QueryResultFactory() {
	}

	public static <T> QueryResult<T> instance(final List<T> items, final long total) {
		return new QueryResultImpl<>(items, total);
	}

	static class QueryResultImpl<T> implements QueryResult<T> {

		private final List<T> items;

		private final long total;

		public QueryResultImpl(final List<T> aItems, final long aTotal) {
			items = aItems;
			total = aTotal;
		}

		@Override
		public List<T> getItems() {
			return items;
		}

		@Override
		public long getTotal() {
			return total;
		}

	}

}
