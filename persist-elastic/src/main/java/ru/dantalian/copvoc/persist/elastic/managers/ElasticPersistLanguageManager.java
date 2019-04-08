package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import joptsimple.internal.Strings;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistLanguageManager;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.elastic.model.DbLanguage;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORM;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORMFactory;
import ru.dantalian.copvoc.persist.impl.model.PojoLanguage;

@Service
public class ElasticPersistLanguageManager implements PersistLanguageManager {

	private static final String DEFAULT_INDEX = "languages";

	@Autowired
	private DefaultSettingsProvider settingsProvider;

	@Autowired
	private ElasticORMFactory ormFactory;

	private ElasticORM<DbLanguage> orm;

	private Map<String, Language> cache = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		orm = ormFactory.newElasticORM(DbLanguage.class, settingsProvider);
	}

	@Override
	public List<Language> listLanguages(final Optional<String> aName, final Optional<String> aCountry,
			final Optional<String> aVariant) throws PersistException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchSourceBuilder.sort("name");
		searchSourceBuilder.sort("country");
		searchSourceBuilder.sort("variant");
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

		final SearchResponse search = orm.search(DEFAULT_INDEX, searchSourceBuilder);
		final List<Language> list = new LinkedList<>();
		search.getHits()
				.forEach(aItem -> {
					final Map<String, Object> src = aItem.getSourceAsMap();
					try {
						list.add(asLanguage(orm.map(aItem.getId(), src)));
					} catch (final PersistException e) {
						throw new RuntimeException("Failed to convert an item " + aItem.getId());
					}
				});
		return list;
	}

	@Override
	public Language getLanguage(final String aName, final String aCountry, final String aVariant)
			throws PersistException {
		final String id = Strings.join(new String[] {aName, aCountry, Optional.ofNullable(aVariant).orElse("")}, "_");
		Language language = cache.get(id);
		if (language == null) {
			language = asLanguage(orm.get(DEFAULT_INDEX, id));
			cache.put(id, language);
		}
		return language;
	}

	@Override
	public Language createLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		final DbLanguage dbLanguage = new DbLanguage(aName, aCountry, aVariant, aText);
		orm.add(DEFAULT_INDEX, dbLanguage, true);
		return asLanguage(dbLanguage);
	}

	@Override
	public Language updateLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		final DbLanguage dbLanguage = new DbLanguage(aName, aCountry, aVariant, aText);
		orm.update(DEFAULT_INDEX, dbLanguage, true);
		return asLanguage(dbLanguage);
	}

	private Language asLanguage(final DbLanguage aLang) {
		if (aLang == null) {
			return null;
		}
		return new PojoLanguage(aLang.getName(), aLang.getCountry(), aLang.getVariant(), aLang.getText());
	}

}
