package ru.dantalian.copvoc.persist.impl.query;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.query.QueryExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortExpression;

public class DefaultQuery implements Query {

	private final UUID vocabularyId;

	private final QueryExpression expression;

	private final List<SortExpression> sort;

	private final Integer from;

	private final Integer limit;

	public DefaultQuery(final UUID aVocabularyId, final QueryExpression aExpression,
			final List<SortExpression> aSort, final Integer aFrom, final Integer aLimit) {
		vocabularyId = aVocabularyId;
		expression = aExpression;
		sort = aSort;
		from = aFrom;
		limit = aLimit;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	@Override
	public QueryExpression where() {
		return expression;
	}

	@Override
	public List<SortExpression> sort() {
		return sort;
	}

	@Override
	public Integer from() {
		return from;
	}

	@Override
	public Integer limit() {
		return limit;
	}

}
