package ru.dantalian.copvoc.persist.elastic.managers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
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
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistLanguageManager;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.elastic.common.IndexHandler;
import ru.dantalian.copvoc.persist.elastic.common.IndexTransaction;
import ru.dantalian.copvoc.persist.elastic.model.DbLanguage;
import ru.dantalian.copvoc.persist.impl.model.PojoLanguage;

@Service
public class SqlitePersistLanguageManager implements PersistLanguageManager, IndexHandler {

	private static final String DEFAULT_INDEX = "languages";

	@Autowired
	private RestHighLevelClient client;

	@Override
	public List<Language> listLanguages(final Optional<String> aName, final Optional<String> aCountry,
			final Optional<String> aVariant) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, false)) {
			final SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			searchSourceBuilder.sort("name");
			searchSourceBuilder.sort("country");
			searchSourceBuilder.sort("variant");
			searchRequest.source(searchSourceBuilder);
			final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			if (aName.isPresent()) {
				boolQuery.must(QueryBuilders.termQuery("name", aName.get()));
			}
			if (aCountry.isPresent()) {
				boolQuery.must(QueryBuilders.termQuery("country", aCountry.get()));
			}
			if (aVariant.isPresent()) {
				boolQuery.must(QueryBuilders.termQuery("variant", aVariant.get()));
			}
			searchSourceBuilder.query(boolQuery);

			final SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			final List<Language> list = new LinkedList<>();
			search.getHits()
					.forEach(aItem -> {
						final Map<String, Object> src = aItem.getSourceAsMap();
						list.add(new PojoLanguage((String) src.get("name"),
								(String) src.get("country"),
								(String) src.get("variant"),
								(String) src.get("text")));
					});
			return list;
		} catch (final Exception e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	@Override
	public Language getLanguage(final String aName, final String aCountry, final String aVariant)
			throws PersistException {
		final List<Language> languages = listLanguages(Optional.ofNullable(aName),
				Optional.ofNullable(aCountry), Optional.ofNullable(aVariant));
		if (languages.iterator().hasNext()) {
			return languages.iterator().next();
		}
		return null;
	}

	@Override
	public Language createLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
			    builder.field("name", aName);
			    builder.field("country", aCountry);
			    builder.field("variant", aVariant);
			    builder.field("text", aText);
			}
			builder.endObject();
			final String id = aName + "_" + aCountry + (aVariant == null || aVariant.isEmpty() ? "" : "_" + aVariant);
			final IndexRequest indexRequest = new IndexRequest(DEFAULT_INDEX, "_doc", id)
	        .source(builder);
			client.index(indexRequest, RequestOptions.DEFAULT);
			return asLanguage(new DbLanguage(aName, aCountry, aVariant, aText));
		} catch (final Exception e) {
			throw new PersistException("Failed create a language", e);
		}
	}

	@Override
	public Language updateLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		try (IndexTransaction tr = IndexTransaction.newInstance(this, true)) {
			final String id = aName + "_" + aCountry + "_" + aVariant;
			final UpdateRequest request = new UpdateRequest(DEFAULT_INDEX, "_doc", id);
			final XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
			    builder.field("name", aName);
			    builder.field("country", aCountry);
			    builder.field("variant", aVariant);
			    builder.field("text", aText);
			}
			builder.endObject();
			request.doc(builder);
			client.update(request, RequestOptions.DEFAULT);
			return asLanguage(new DbLanguage(aName, aCountry, aVariant, aText));
		} catch (final Exception e) {
			throw new PersistException("Failed create a language", e);
		}
	}

	@Override
	public void commit() throws ElasticsearchException, IOException {
		final RefreshRequest refresh = new RefreshRequest(DEFAULT_INDEX);
		client.indices().refresh(refresh, RequestOptions.DEFAULT);
		final FlushRequest flush = new FlushRequest(DEFAULT_INDEX);
		client.indices().flush(flush, RequestOptions.DEFAULT);
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
            builder.startObject("name");
              builder.field("type", "keyword");
            builder.endObject();
            builder.startObject("country");
            	builder.field("type", "keyword");
            builder.endObject();
            builder.startObject("variant");
            	builder.field("type", "keyword");
            builder.endObject();
            builder.startObject("text");
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
	}

	private Language asLanguage(final DbLanguage aLang) {
		return new PojoLanguage(aLang.getName(), aLang.getCountry(), aLang.getVariant(), aLang.getText());
	}

}
