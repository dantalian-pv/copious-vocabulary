package ru.dantalian.copvoc.persist.api.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.Builder;
import ru.dantalian.copvoc.persist.api.query.sort.SortExpression;

public interface QueryBuilder extends Builder<Query> {

	QueryBuilder setVocabularyId(UUID aVocabularyId);

	QueryBuilder where(QueryExpression aExpression);

	QueryBuilder addSort(SortExpression aSortExpression);

	QueryBuilder from(Integer aFrom);

	QueryBuilder limit(Integer aLimit);

}
