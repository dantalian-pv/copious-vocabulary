package ru.dantalian.copvoc.persist.elastic.managers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.elastic.common.IndexHandler;
import ru.dantalian.copvoc.persist.elastic.common.IndexTransaction;
import ru.dantalian.copvoc.persist.elastic.model.DbVocabulary;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabulary;

@Service
public class ElasticPersistVocabularyManager implements PersistVocabularyManager, IndexHandler {

	private static final String DEFAULT_INDEX = "vocabularies";

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private SqlitePersistLanguageManager mLangManager;

	@Override
	public Vocabulary createVocabulary(final String aUser, final String aName, final String aDescription,
			final Language aSource, final Language aTarget) throws PersistException {
		final UUID uuid = UUID.randomUUID();
		final String id = uuid.toString();
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
			    builder.field("name", aName);
			    builder.field("description", aDescription);
			    builder.field("user", aUser);
			    builder.field("source", LanguageUtils.asString(aSource));
			    builder.field("target", LanguageUtils.asString(aTarget));
			}
			builder.endObject();

			final IndexRequest indexRequest = new IndexRequest(DEFAULT_INDEX,
					"_doc",
					id)
	        .source(builder);
			client.index(indexRequest, RequestOptions.DEFAULT);
			return new PojoVocabulary(uuid, aName, aDescription, aUser, aSource, aTarget);
		} catch (final Exception e) {
			throw new PersistException("Failed create a card", e);
		}
	}

	@Override
	public void updateVocabulary(final String aUser, final Vocabulary aVocabulary) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final UpdateRequest request = new UpdateRequest(DEFAULT_INDEX, "_doc", aVocabulary.getId().toString());
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
				 builder.field("name", aVocabulary.getName());
			    builder.field("description", aVocabulary.getDescription());
			    builder.field("user", aUser);
			    builder.field("source", LanguageUtils.asString(aVocabulary.getSource()));
			    builder.field("target", LanguageUtils.asString(aVocabulary.getTarget()));
			}
			builder.endObject();
			request.doc(builder);
			client.update(request, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public Vocabulary getVocabulary(final String aUser, final UUID aId) throws PersistException {
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
			Language source = LanguageUtils.asLanguage((String) src.get("source"));
			Language target = LanguageUtils.asLanguage((String) src.get("target"));
			source = mLangManager.getLanguage(source.getName(), source.getCountry(), source.getVariant());
			target = mLangManager.getLanguage(target.getName(), target.getCountry(), target.getVariant());
			return new PojoVocabulary(UUID.fromString(response.getId()),
					(String) src.get("name"),
					(String) src.get("description"),
					(String) src.get("user"),
					source,
					target);
		} catch (final Exception e) {
			throw new PersistException("Failed to update a card", e);
		}
	}

	@Override
	public Vocabulary queryVocabulary(final String aUser, final String aName) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			final BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("user", aUser))
				.must(QueryBuilders.termQuery("name", aName));
			searchSourceBuilder.query(query);
			searchRequest.source(searchSourceBuilder);

			final SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			final Iterator<SearchHit> iterator = search.getHits().iterator();
			if (!iterator.hasNext()) {
				return null;
			}
			final SearchHit hit = iterator.next();
			Language source = LanguageUtils.asLanguage(hit.field("source").getValue());
			Language target = LanguageUtils.asLanguage(hit.field("target").getValue());
			source = mLangManager.getLanguage(source.getName(), source.getCountry(), source.getVariant());
			target = mLangManager.getLanguage(target.getName(), target.getCountry(), target.getVariant());
			final Map<String, Object> src = hit.getSourceAsMap();
			return new PojoVocabulary(UUID.fromString(hit.getId()),
					(String) src.get("name"),
					(String) src.get("description"),
					(String) src.get("user"),
					source, target);
		} catch (final Exception e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	@Override
	public List<Vocabulary> listVocabularies(final String aUser) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.termQuery("user", aUser));
			searchRequest.source(searchSourceBuilder);

			final SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			final List<Vocabulary> list = new LinkedList<>();
			search.getHits()
				.forEach(aItem -> {
					Language source = LanguageUtils.asLanguage(aItem.field("source").getValue());
					Language target = LanguageUtils.asLanguage(aItem.field("target").getValue());
					try {
						source = mLangManager.getLanguage(source.getName(), source.getCountry(), source.getVariant());
						target = mLangManager.getLanguage(target.getName(), target.getCountry(), target.getVariant());
					} catch (final PersistException e) {
						throw new IllegalStateException("Failed to get languge", e);
					}
					final Map<String, Object> src = aItem.getSourceAsMap();
					final PojoVocabulary vocabulary = new PojoVocabulary(UUID.fromString(aItem.getId()),
							(String) src.get("name"),
							(String) src.get("description"),
							(String) src.get("user"),
							source, target);
					list.add(vocabulary);
				});
			return list;
		} catch (final Exception e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	private Vocabulary toVocabularySilent(final DbVocabulary aVocabulary) {
		try {
			return toVocabulary(aVocabulary);
		} catch (final PersistException e) {
			throw new RuntimeException("Failed to convert " + aVocabulary, e);
		}
	}

	private Vocabulary toVocabulary(final DbVocabulary aDbCardVocabulary) throws PersistException {
		if (aDbCardVocabulary == null) {
			return null;
		}
		Language source = LanguageUtils.asLanguage(aDbCardVocabulary.getSource());
		Language target = LanguageUtils.asLanguage(aDbCardVocabulary.getTarget());
		source = mLangManager.getLanguage(source.getName(), source.getCountry(), source.getVariant());
		target = mLangManager.getLanguage(target.getName(), target.getCountry(), target.getVariant());
		return new PojoVocabulary(aDbCardVocabulary.getId(), aDbCardVocabulary.getName(), aDbCardVocabulary.getDescription(),
				aDbCardVocabulary.getUser(), source, target);
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
          builder.startObject("name");
            builder.field("type", "keyword");
          builder.endObject();
          builder.startObject("description");
          	builder.field("type", "text");
          builder.endObject();
          builder.startObject("user");
          	builder.field("type", "keyword");
          builder.endObject();
          builder.startObject("source");
        		builder.field("type", "keyword");
        	builder.endObject();
        	builder.startObject("target");
        		builder.field("type", "keyword");
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
