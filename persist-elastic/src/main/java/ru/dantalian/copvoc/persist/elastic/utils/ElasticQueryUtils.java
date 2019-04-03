package ru.dantalian.copvoc.persist.elastic.utils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType;
import org.elasticsearch.search.sort.SortBuilders;

import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatAction;
import ru.dantalian.copvoc.persist.api.query.BoolExpression;
import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.query.QueryExpression;
import ru.dantalian.copvoc.persist.api.query.TermExpression;
import ru.dantalian.copvoc.persist.api.query.ValueExpression;
import ru.dantalian.copvoc.persist.api.query.sort.FieldSortExpression;
import ru.dantalian.copvoc.persist.api.query.sort.ScriptSortExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortExpression;
import ru.dantalian.copvoc.persist.api.query.sort.SortOrder;
import ru.dantalian.copvoc.persist.api.stats.StatAction;
import ru.dantalian.copvoc.persist.api.utils.Validator;

public final class ElasticQueryUtils {

	private ElasticQueryUtils() {
	}

	public static QueryBuilder asElaticQuery(final Query aQuery) {
		final BoolQueryBuilder bool = QueryBuilders.boolQuery();
		if (aQuery.getVocabularyId() != null) {
			bool.must(QueryBuilders.termQuery("vocabulary_id", aQuery.getVocabularyId().toString()));
		}
		if (aQuery.where() != null) {
			bool.must(asElaticQuery(aQuery.where()));
		}
		return bool.must().isEmpty() ? QueryBuilders.matchAllQuery() : bool;
	}

	public static QueryBuilder asElaticQuery(final QueryExpression aExpression) {
		if (aExpression instanceof TermExpression) {
			final TermExpression termExpression = (TermExpression) aExpression;
			if (termExpression.isWildcard()) {
				return QueryBuilders.wildcardQuery(termExpression.getName(), termExpression.getValue());
			} else {
				return QueryBuilders.termQuery(termExpression.getName(), termExpression.getValue());
			}
		} else if (aExpression instanceof ValueExpression) {
			final ValueExpression valueExpression = (ValueExpression) aExpression;
			return QueryBuilders.termQuery(valueExpression.getName(), valueExpression.getValue());
		} else if (aExpression instanceof BoolExpression) {
			final BoolQueryBuilder boolElastic = QueryBuilders.boolQuery();
			final BoolExpression boolExpression = (BoolExpression) aExpression;
			for (final QueryExpression exp: boolExpression.must()) {
				boolElastic.must(asElaticQuery(exp));
			}
			for (final QueryExpression exp: boolExpression.should()) {
				boolElastic.should(asElaticQuery(exp));
			}
			for (final QueryExpression exp: boolExpression.not()) {
				boolElastic.mustNot(asElaticQuery(exp));
			}
			return boolElastic;
		} else {
			throw new IllegalArgumentException("Unknown expression type: " + aExpression.getClass().getName());
		}
	}

	public static void addSort(final SearchSourceBuilder aSearchSourceBuilder, final List<SortExpression> aSort) {
		for (final SortExpression sort: aSort) {
			final org.elasticsearch.search.sort.SortOrder order = sort.getOrder() == SortOrder.ASC
					? org.elasticsearch.search.sort.SortOrder.ASC : org.elasticsearch.search.sort.SortOrder.DESC;
			switch (sort.getType()) {
				case FIELD: {
					final FieldSortExpression fieldSort = (FieldSortExpression) sort;
					aSearchSourceBuilder.sort(SortBuilders.fieldSort(fieldSort.getField()).order(order));
					break;
				}
				case RANDOM: {
					aSearchSourceBuilder.sort(SortBuilders.scriptSort(new Script("Math.random()"), ScriptSortType.NUMBER)
							.order(org.elasticsearch.search.sort.SortOrder.ASC));
					break;
				}
				case SCORE: {
					aSearchSourceBuilder.sort(SortBuilders.scoreSort().order(order));
					break;
				}
				case SCRIPT: {
					final ScriptSortExpression scriptSort = (ScriptSortExpression) sort;
					final Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG,
							scriptSort.getScript(), scriptSort.getParams());
					aSearchSourceBuilder.sort(SortBuilders.scriptSort(script, ScriptSortType.NUMBER)
							.order(order));
					break;
				}
				default:
					throw new IllegalArgumentException("Unknown sort type " + sort.getType());
			}
		}
	}

	public static void setFromAndLimit(final SearchSourceBuilder searchSourceBuilder,
			final Integer aFrom, final Integer aLimit) {
		if (aFrom != null) {
			searchSourceBuilder.from(aFrom.intValue());
		}
		if (aLimit != null) {
			searchSourceBuilder.size(aLimit.intValue());
		}
	}

	public static Script asElasticScript(final CardStatAction aAction) {
		Validator.checkNotNull(aAction);
		final String action = asStringAction(aAction);
		final String field = "ctx._source.stats." + CardUtils.asPersistStatName(aAction);
		final Object defaultValue = getDefaultValue(aAction);
		final Map<String, Object> params = Collections.singletonMap("value", aAction.getValue());

		final String script = generateScript(aAction, action, field, defaultValue);

		return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, script, params);
	}

	private static String generateScript(final CardStatAction aAction, final String action,
			final String field, final Object defaultValue) {
		return aAction.getAction() == StatAction.SET ?
				field + "=params.value" :
					field + "=" + field + " == null "
					+ "? " + defaultValue + action + "params.value"
					+ " : " + field + action + "params.value";
	}

	private static Object getDefaultValue(final CardStat aStat) {
		switch (aStat.getType()) {
			case DATE: {
				return new Date();
			}
			case DOUBLE: {
				return 0.0D;
			}
			case LONG: {
				return 0L;
			}
			default:
				throw new IllegalArgumentException("Unknown type " + aStat.getType());
		}
	}

	private static String asStringAction(final CardStatAction aAction) {
		switch (aAction.getAction()) {
			case DECREMENT: {
				return "-";
			}
			case ICREMENT: {
				return "+";
			}
			case SET: {
				return "=";
			}
			default:
				throw new IllegalArgumentException("Unknown action type " + aAction.getAction());
		}
	}

}
