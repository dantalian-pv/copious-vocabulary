package ru.dantalian.copvoc.persist.api.query;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.query.sort.SortExpression;

public interface Query {

	UUID getVocabularyId();

	QueryExpression where();

	List<SortExpression> sort();

	Integer from();

	Integer limit();

}
