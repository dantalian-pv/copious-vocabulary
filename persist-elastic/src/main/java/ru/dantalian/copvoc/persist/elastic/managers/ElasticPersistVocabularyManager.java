package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
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
import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;
import ru.dantalian.copvoc.persist.elastic.model.DbVocabulary;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabulary;

@Service
public class ElasticPersistVocabularyManager extends AbstractPersistManager<DbVocabulary>
	implements PersistVocabularyManager {

	private static final String DEFAULT_INDEX = "vocabularies";

	private final ElasticPersistLanguageManager mLangManager;

	private final ElasticSettings settings;

	@Autowired
	public ElasticPersistVocabularyManager(final RestHighLevelClient aClient,
			final ElasticPersistLanguageManager aLangManager,
			final ElasticSettings aSettings) {
		super(aClient, DbVocabulary.class);
		mLangManager = aLangManager;
		settings = aSettings;
	}

	@Override
	protected String getDefaultIndex() {
		return DEFAULT_INDEX;
	}

	@Override
	protected XContentBuilder getSettings(final String aIndex) throws PersistException {
		return settings.getDefaultSettings();
	}

	@Override
	public Vocabulary createVocabulary(final String aUser, final String aName, final String aDescription,
			final Language aSource, final Language aTarget) throws PersistException {
		final UUID uuid = UUID.randomUUID();
		final String source = LanguageUtils.asString(aSource);
		final String target = LanguageUtils.asString(aTarget);
		final DbVocabulary voc = new DbVocabulary(uuid, aName, aDescription, aUser, source, target);
		add(DEFAULT_INDEX, voc, true);
		return asVocabulary(voc);
	}

	@Override
	public void updateVocabulary(final String aUser, final Vocabulary aVocabulary) throws PersistException {
		final DbVocabulary voc = asDbVocabulary(aVocabulary);
		update(DEFAULT_INDEX, voc, true);
	}

	@Override
	public Vocabulary getVocabulary(final String aUser, final UUID aId) throws PersistException {
		return asVocabulary(get(DEFAULT_INDEX, aId.toString()));
	}

	@Override
	public Vocabulary queryVocabulary(final String aUser, final String aName) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		final BoolQueryBuilder query = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery("user", aUser))
			.must(QueryBuilders.termQuery("name", aName));
		searchSourceBuilder.query(query);

		final SearchResponse search = search(DEFAULT_INDEX, searchSourceBuilder);
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
	}

	@Override
	public List<Vocabulary> listVocabularies(final String aUser) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.termQuery("user", aUser));

		final SearchResponse search = search(DEFAULT_INDEX, searchSourceBuilder);
		final List<Vocabulary> list = new LinkedList<>();
		search.getHits()
			.forEach(aItem -> {
				final Map<String, Object> src = aItem.getSourceAsMap();
				Language source = LanguageUtils.asLanguage((String) src.get("source"));
				Language target = LanguageUtils.asLanguage((String) src.get("target"));
				try {
					source = mLangManager.getLanguage(source.getName(), source.getCountry(), source.getVariant());
					target = mLangManager.getLanguage(target.getName(), target.getCountry(), target.getVariant());
				} catch (final PersistException e) {
					throw new IllegalStateException("Failed to get languge", e);
				}
				try {
					list.add(asVocabulary(map(aItem.getId(), src)));
				} catch (final PersistException e) {
					throw new RuntimeException("Failed to convert " + aItem.getId(), e);
				}
			});
		return list;
	}

	private Vocabulary asVocabulary(final DbVocabulary aDbCardVocabulary) throws PersistException {
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

	private DbVocabulary asDbVocabulary(final Vocabulary aVocabulary) {
		final String source = LanguageUtils.asString(aVocabulary.getSource());
		final String target = LanguageUtils.asString(aVocabulary.getTarget());
		return new DbVocabulary(aVocabulary.getId(), aVocabulary.getName(), aVocabulary.getDescription(),
				aVocabulary.getUser(), source, target);
	}

}
