package ru.dantalian.copvoc.persist.impl.query;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.query.QueryBuilder;
import ru.dantalian.copvoc.persist.api.query.QueryExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortExpression;

public class DefaultQueryBuilder implements QueryBuilder {

	private UUID vocabularyId;
	private QueryExpression expression;
	private List<SortExpression> sort = new LinkedList<>();

	private Integer from;
	private Integer limit;

	@Override
	public QueryBuilder setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
		return this;
	}

	@Override
	public QueryBuilder where(final QueryExpression aExpression) {
		expression = aExpression;
		return this;
	}

	@Override
	public QueryBuilder addSort(final SortExpression aSortExpression) {
		sort.add(aSortExpression);
		return this;
	}

	@Override
	public QueryBuilder from(final Integer aFrom) {
		from = aFrom;
		return this;
	}

	@Override
	public QueryBuilder limit(final Integer aLimit) {
		limit = aLimit;
		return this;
	}

	@Override
	public Query build() throws PersistException {
		return new DefaultQuery(vocabularyId, expression, sort, from, limit);
	}

}
