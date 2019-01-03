package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.elastic.model.DbCardField;
import ru.dantalian.copvoc.persist.impl.model.PojoCardField;

@Service
public class ElasticPersistCardFieldManager extends AbstractPersistManager<DbCardField>
	implements PersistCardFieldManager {

	private static final String DEFAULT_INDEX = "fields";

	@Autowired
	public ElasticPersistCardFieldManager(final RestHighLevelClient aClient) {
		super(aClient, DbCardField.class);
	}

	@Override
	protected String getDefaultIndex() {
		return DEFAULT_INDEX;
	}

	@Override
	protected XContentBuilder getSettings(final String aIndex) throws PersistException {
		return null;
	}

	@Override
	public CardField createField(final String aUser, final UUID aVocabularyId, final String aName,
			final CardFiledType aType) throws PersistException {
		final DbCardField cardField = new DbCardField(aVocabularyId, aName, aType);
		add(DEFAULT_INDEX, cardField, true);
		return asCardField(cardField);
	}

	@Override
	public CardField getField(final String aUser, final UUID aVocabularyId, final String aName) throws PersistException {
		final DbCardField cardField = new DbCardField(aVocabularyId, aName, null);
		return asCardField(get(DEFAULT_INDEX, cardField.getId()));
	}

	@Override
	public void deleteField(final String aUser, final UUID aVocabularyId, final String aName) throws PersistException {
		final DbCardField cardField = new DbCardField(aVocabularyId, aName, null);
		delete(DEFAULT_INDEX, cardField.getId());
	}

	@Override
	public List<CardField> listFields(final String aUser, final UUID aVocabularyId) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.termQuery("vocabulary_id", aVocabularyId.toString()));

		final SearchResponse search = search(DEFAULT_INDEX, searchSourceBuilder);
		final List<CardField> list = new LinkedList<>();
		search.getHits()
				.forEach(aItem -> {
					final Map<String, Object> src = aItem.getSourceAsMap();
					try {
						list.add(map(src));
					} catch (final PersistException e) {
						throw new RuntimeException("Failed to convert item " + aItem.getId());
					}
					});
		return list;
	}

	private CardField asCardField(final DbCardField aCardField) {
		return new PojoCardField(aCardField.getVocabularyId(), aCardField.getName(), aCardField.getType());
	}

}
