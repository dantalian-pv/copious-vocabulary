package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.elastic.model.DbCardField;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORM;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORMFactory;
import ru.dantalian.copvoc.persist.impl.model.PojoCardField;

@Service
public class ElasticPersistCardFieldManager implements PersistCardFieldManager {

	private static final String DEFAULT_INDEX = "fields";

	@Autowired
	private DefaultSettingsProvider settingsProvider;

	@Autowired
	private ElasticORMFactory ormFactory;

	private ElasticORM<DbCardField> orm;

	@PostConstruct
	public void init() {
		orm = ormFactory.newElasticORM(DbCardField.class, settingsProvider);
	}

	@Override
	public CardField createField(final String aUser, final UUID aVocabularyId, final String aName,
			final CardFiledType aType, final Integer aOrder, final boolean aSystem) throws PersistException {
		final DbCardField cardField = new DbCardField(aVocabularyId, aName, aType, aOrder, aSystem);
		orm.add(DEFAULT_INDEX, cardField, true);
		return asCardField(cardField);
	}

	@Override
	public CardField getField(final String aUser, final UUID aVocabularyId, final String aName) throws PersistException {
		final DbCardField cardField = new DbCardField(aVocabularyId, aName, null, null, false);
		return asCardField(orm.get(DEFAULT_INDEX, cardField.getId()));
	}

	@Override
	public void deleteField(final String aUser, final UUID aVocabularyId, final String aName) throws PersistException {
		final DbCardField cardField = new DbCardField(aVocabularyId, aName, null, null, false);
		orm.delete(DEFAULT_INDEX, cardField.getId());
	}

	@Override
	public List<CardField> listFields(final String aUser, final UUID aVocabularyId) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (aVocabularyId != null) {
			searchSourceBuilder.query(QueryBuilders.termQuery("vocabulary_id", aVocabularyId.toString()));
		}
		// Sort fields by order
		searchSourceBuilder.sort(SortBuilders.fieldSort("order").order(SortOrder.ASC));

		final SearchResponse search = orm.search(DEFAULT_INDEX, searchSourceBuilder);
		final List<CardField> list = new LinkedList<>();
		search.getHits()
				.forEach(aItem -> {
					final Map<String, Object> src = aItem.getSourceAsMap();
					try {
						list.add(orm.map(aItem.getId(), src));
					} catch (final PersistException e) {
						throw new RuntimeException("Failed to convert item " + aItem.getId());
					}
					});
		return list;
	}

	private CardField asCardField(final DbCardField aCardField) {
		if (aCardField == null) {
			return null;
		}
		return new PojoCardField(aCardField.getVocabularyId(), aCardField.getName(),
				aCardField.getType(), aCardField.getOrder(), aCardField.isSystem());
	}

}
