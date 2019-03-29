package ru.dantalian.copvoc.persist.impl.query;

import java.util.Collections;
import java.util.Map;

import ru.dantalian.copvoc.persist.api.query.BoolExpressionBuilder;
import ru.dantalian.copvoc.persist.api.query.QueryBuilder;
import ru.dantalian.copvoc.persist.api.query.TermExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortOrder;
import ru.dantalian.copvoc.persist.api.query.sort.SortType;
import ru.dantalian.copvoc.persist.impl.query.sort.FieldSortExpressionImpl;
import ru.dantalian.copvoc.persist.impl.query.sort.ScriptSortExpressionImpl;

public class QueryFactory {

	private QueryFactory() {
	}

	public static QueryBuilder newCardsQuery() {
		return new DefaultQueryBuilder();
	}

	public static TermExpression term(final String aKey, final String aValue, final boolean aWildcard) {
		return new TermExpressionImpl(aKey, aValue, aWildcard);
	}

	public static BoolExpressionBuilder bool() {
		return new BoolExpressionBuilderImpl();
	}

	public static SortExpression sortScore(final SortOrder aOrder) {
		return new FieldSortExpressionImpl(SortType.SCORE, aOrder, null);
	}

	public static SortExpression sortRandom() {
		return new ScriptSortExpressionImpl(SortType.RANDOM, null, null, Collections.emptyMap());
	}

	public static SortExpression sortScript(final SortOrder aOrder, final String aScript,
			final Map<String, Object> aParams) {
		return new ScriptSortExpressionImpl(SortType.SCRIPT, aOrder, aScript,
				aParams == null ? Collections.emptyMap() : aParams);
	}

	public static SortExpression sortField(final SortOrder aOrder, final String aField) {
		return new FieldSortExpressionImpl(SortType.SCRIPT, aOrder, aField);
	}

}
