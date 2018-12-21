package ru.dantalian.copvoc.persist.elastic.managers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.elastic.common.IndexHandler;
import ru.dantalian.copvoc.persist.elastic.common.IndexTransaction;
import ru.dantalian.copvoc.persist.impl.model.PojoCardField;

@Service
public class SqlitePersistCardFieldManager implements PersistCardFieldManager, IndexHandler {

	private static final String DEFAULT_INDEX = "fields";

	@Autowired
	private RestHighLevelClient client;

	@Override
	public CardField createField(final String aUser, final UUID aVocabularyId, final String aName,
			final CardFiledType aType) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final String id = id(aVocabularyId, aName);
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
			    builder.field("vocabulary_id", aVocabularyId.toString());
			    builder.field("name", aName);
			    builder.field("type", aType.name());
			}
			builder.endObject();

			final IndexRequest indexRequest = new IndexRequest(DEFAULT_INDEX,
					"_doc",
					id)
	        .source(builder);
			client.index(indexRequest, RequestOptions.DEFAULT);
			return new PojoCardField(aVocabularyId, aName, aType);
		} catch (final Exception e) {
			throw new PersistException("Failed create a card", e);
		}
	}

	private String id(final UUID aVocabularyId, final String aName) {
		return aVocabularyId.toString() + "_" + aName;
	}

	@Override
	public CardField getField(final String aUser, final UUID aVocabularyId, final String aName) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final GetRequest getRequest = new GetRequest(
	        DEFAULT_INDEX,
	        "_doc",
	        id(aVocabularyId, aName));
			final GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
			if (!response.isExists()) {
				return null;
			}
			return new PojoCardField(aVocabularyId, aName, CardFiledType.valueOf(response.getField("type").getValue()));
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public void deleteField(final String aUser, final UUID aVocabularyId, final String aName) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final DeleteRequest request = new DeleteRequest(
	        DEFAULT_INDEX,
	        "_doc",
	        id(aVocabularyId, aName));
			client.delete(request, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public List<CardField> listFields(final String aUser, final UUID aVocabularyId) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.termQuery("vocabulary_id", aVocabularyId.toString()));
			searchRequest.source(searchSourceBuilder);

			final SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			final List<CardField> list = new LinkedList<>();
			search.getHits()
					.forEach(aItem -> list.add(new PojoCardField(
							UUID.fromString(aItem.field("vocabulary_id").getValue()),
							aItem.field("name").getValue(),
							CardFiledType.valueOf(aItem.field("type").getValue()))));
			return list;
		} catch (final Exception e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	@Override
	public void initIndex() throws ElasticsearchException, IOException {
		final GetIndexRequest exists = new GetIndexRequest();
		exists.indices(DEFAULT_INDEX);
		if (client.indices().exists(exists, RequestOptions.DEFAULT)) {
			return;
		}
		final CreateIndexRequest createIndex = new CreateIndexRequest(DEFAULT_INDEX);
		final XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		{
	    builder.startObject("_doc");
	    {
        builder.startObject("properties");
        {
          builder.startObject("vocabulary_id");
            builder.field("type", "keyword");
          builder.endObject();
          builder.startObject("name");
          	builder.field("type", "keyword");
          builder.endObject();
          builder.startObject("type");
        		builder.field("type", "keyword");
        	builder.endObject();
        }
        builder.endObject();
	    }
	    builder.endObject();
		}
		builder.endObject();
		createIndex.mapping("_doc", builder);
	}

	@Override
	public void commit() throws ElasticsearchException, IOException {
		final RefreshRequest refresh = new RefreshRequest(DEFAULT_INDEX);
		client.indices().refresh(refresh, RequestOptions.DEFAULT);
		final FlushRequest flush = new FlushRequest(DEFAULT_INDEX);
		client.indices().flush(flush, RequestOptions.DEFAULT);
	}

}
