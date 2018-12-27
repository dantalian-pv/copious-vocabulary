package ru.dantalian.copvoc.persist.elastic.managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;
import ru.dantalian.copvoc.persist.elastic.common.IndexHandler;
import ru.dantalian.copvoc.persist.elastic.common.IndexTransaction;
import ru.dantalian.copvoc.persist.elastic.model.DbCard;
import ru.dantalian.copvoc.persist.impl.model.PojoCard;
import ru.dantalian.copvoc.persist.impl.model.PojoCardFieldContent;

@Service
public class SqlitePersistCardManager implements PersistCardManager, IndexHandler {

	private static final String DEFAULT_INDEX = "cards";

	@Autowired
	private RestHighLevelClient client;

	@Override
	public Card createCard(final String aUser, final UUID aVocabularyId,
			final Map<String, String> aContent) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final UUID id = UUID.randomUUID();
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
			    builder.field("vocabulary_id", aVocabularyId.toString());
			    builder.field("fields_content", aContent);
			}
			builder.endObject();

			final IndexRequest indexRequest = new IndexRequest(DEFAULT_INDEX,
					"_doc",
					id.toString())
	        .source(builder);
			client.index(indexRequest, RequestOptions.DEFAULT);
			return asCard(new DbCard(id, aVocabularyId, aContent));
		} catch (final Exception e) {
			throw new PersistException("Failed create a card", e);
		}
	}

	@Override
	public void updateCard(final String aUser, final UUID aId, final Map<String, String> aContent)
			throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final Card card = getCard(aUser, aId);
			if (card == null) {
				throw new PersistException("Card not found");
			}
			final UpdateRequest request = new UpdateRequest(DEFAULT_INDEX, "_doc", aId.toString());
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
			    builder.field("vocabulary_id", card.getVocabularyId().toString());
			    builder.field("fields_content", aContent);
			}
			builder.endObject();
			request.doc(builder);
			client.update(request, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public Card getCard(final String aUser, final UUID aId) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final GetRequest getRequest = new GetRequest(
	        DEFAULT_INDEX,
	        "_doc",
	        aId.toString());
			final GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
			if (!response.isExists()) {
				return null;
			}
			return new PojoCard(UUID.fromString(response.getId()),
					UUID.fromString(response.getField("vocabulary_id").getValue()),
					response.getField("fields_content").getValue());
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public void deleteCard(final String aUser, final UUID aId) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final DeleteRequest request = new DeleteRequest(
	        DEFAULT_INDEX,
	        "_doc",
	        aId.toString());
			client.delete(request, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public List<Card> queryCards(final String aUser, final CardsQuery aQuery) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.termQuery("vocabulary_id", aQuery.getVocabularyId().toString()));
			searchRequest.source(searchSourceBuilder);

			final SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			final List<Card> list = new LinkedList<>();
			search.getHits()
					.forEach(aItem -> list.add(new PojoCard(UUID.fromString(aItem.getId()),
							UUID.fromString(aItem.field("vocabulary_id").getValue()),
							aItem.field("fields_content").getValue())));
			return list;
		} catch (final Exception e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	private Card asCard(final DbCard aDbCard) {
		final Map<String, String> fieldsContent = aDbCard.getFieldsContent();
		final Map<String, CardFieldContent> map = new HashMap<>();
		for (final Entry<String, String> entry: fieldsContent.entrySet()) {
			map.put(entry.getKey(), new PojoCardFieldContent(aDbCard.getId(), aDbCard.getVocabularyId(),
					entry.getKey(), entry.getValue()));
		}
		return new PojoCard(aDbCard.getId(), aDbCard.getVocabularyId(), map);
	}

	@Override
	public void initIndex() throws ElasticsearchException, IOException {
		final GetIndexRequest exists = new GetIndexRequest();
		exists.indices(DEFAULT_INDEX);
		if (!client.indices().exists(exists, RequestOptions.DEFAULT)) {
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
            builder.startObject("fields_content");
            	builder.field("dynamic", true);
            	builder.startObject("properties");
            	builder.endObject();
            builder.endObject();
	        }
	        builder.endObject();
		    }
		    builder.endObject();
			}
			builder.endObject();
			createIndex.mapping("_doc", builder);
			client.indices().create(createIndex, RequestOptions.DEFAULT);
		}
	}

	@Override
	public void commit() throws ElasticsearchException, IOException {
		final RefreshRequest refresh = new RefreshRequest(DEFAULT_INDEX);
		client.indices().refresh(refresh, RequestOptions.DEFAULT);
		final FlushRequest flush = new FlushRequest(DEFAULT_INDEX);
		client.indices().flush(flush, RequestOptions.DEFAULT);
	}

}
