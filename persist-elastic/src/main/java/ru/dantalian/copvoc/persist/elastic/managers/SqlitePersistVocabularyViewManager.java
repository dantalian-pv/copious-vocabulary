package ru.dantalian.copvoc.persist.elastic.managers;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.elastic.common.IndexHandler;
import ru.dantalian.copvoc.persist.elastic.common.IndexTransaction;
import ru.dantalian.copvoc.persist.elastic.model.DbVocabularyView;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabularyView;

@Service
public class SqlitePersistVocabularyViewManager implements PersistVocabularyViewManager, IndexHandler {

	private static final String DEFAULT_INDEX = "views";

	@Autowired
	private RestHighLevelClient client;

	@Override
	public VocabularyView createVocabularyView(final String aUser, final UUID aVocabularyId, final String aCss, final String aFrontTpl,
			final String aBackTpl) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
			    builder.field("css", aCss);
			    builder.field("front", aFrontTpl);
			    builder.field("back", aBackTpl);
			}
			builder.endObject();

			final IndexRequest indexRequest = new IndexRequest(DEFAULT_INDEX,
					"_doc",
					aVocabularyId.toString())
	        .source(builder);
			client.index(indexRequest, RequestOptions.DEFAULT);
			return new PojoVocabularyView(aVocabularyId, aCss, aFrontTpl, aBackTpl);
		} catch (final Exception e) {
			throw new PersistException("Failed create a card", e);
		}
	}

	@Override
	public void updateVocabularyView(final String aUser, final UUID aId, final String aCss, final String aFrontTpl, final String aBackTpl)
			throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final UpdateRequest request = new UpdateRequest(DEFAULT_INDEX, "_doc", aId.toString());
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
				builder.field("css", aCss);
		    builder.field("front", aFrontTpl);
		    builder.field("back", aBackTpl);
			}
			builder.endObject();
			request.doc(builder);
			client.update(request, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public VocabularyView getVocabularyView(final String aUser, final UUID aId) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final GetRequest getRequest = new GetRequest(
	        DEFAULT_INDEX,
	        "_doc",
	        aId.toString());
			final GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
			if (!response.isExists()) {
				return null;
			}
			final Map<String, Object> src = response.getSourceAsMap();
			return new PojoVocabularyView(UUID.fromString(response.getId()),
					(String) src.get("css"),
					(String) src.get("front"),
					(String) src.get("back"));
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	private VocabularyView toCardVocabularyView(final DbVocabularyView aDbCardVocabularyView) {
		if (aDbCardVocabularyView == null) {
			return null;
		}
		return new PojoVocabularyView(aDbCardVocabularyView.getVocabularyId(),
				aDbCardVocabularyView.getCss(), aDbCardVocabularyView.getFrontTpl(), aDbCardVocabularyView.getBackTpl());
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
          builder.startObject("css");
            builder.field("type", "text");
            builder.field("index", false);
          builder.endObject();
          builder.startObject("front");
          	builder.field("type", "text");
          	builder.field("index", false);
          builder.endObject();
          builder.startObject("back");
        		builder.field("type", "text");
        		builder.field("index", false);
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

	@Override
	public void commit() throws ElasticsearchException, IOException {
		final RefreshRequest refresh = new RefreshRequest(DEFAULT_INDEX);
		client.indices().refresh(refresh, RequestOptions.DEFAULT);
		final FlushRequest flush = new FlushRequest(DEFAULT_INDEX);
		client.indices().flush(flush, RequestOptions.DEFAULT);
	}

}
