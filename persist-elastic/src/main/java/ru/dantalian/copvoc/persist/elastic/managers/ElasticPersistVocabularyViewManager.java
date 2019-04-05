package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.elastic.model.DbVocabularyView;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORM;
import ru.dantalian.copvoc.persist.elastic.orm.ElasticORMFactory;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabularyView;

@Service
public class ElasticPersistVocabularyViewManager implements PersistVocabularyViewManager {

	private static final String DEFAULT_INDEX = "views";

	@Autowired
	private DefaultSettingsProvider settingsProvider;

	@Autowired
	private ElasticORMFactory ormFactory;

	private ElasticORM<DbVocabularyView> orm;

	@PostConstruct
	public void init() {
		orm = ormFactory.newElasticORM(DbVocabularyView.class, settingsProvider);
	}

	@Override
	public VocabularyView createVocabularyView(final String aUser, final UUID aVocabularyId, final String aCss, final String aFrontTpl,
			final String aBackTpl) throws PersistException {
		final DbVocabularyView vocView = new DbVocabularyView(aVocabularyId, aCss, aFrontTpl, aBackTpl);
		orm.add(DEFAULT_INDEX, vocView, true);
		return asVocabularyView(vocView);
	}

	@Override
	public void updateVocabularyView(final String aUser, final UUID aId, final String aCss, final String aFrontTpl, final String aBackTpl)
			throws PersistException {
		final DbVocabularyView vocView = new DbVocabularyView(aId, aCss, aFrontTpl, aBackTpl);
		orm.update(DEFAULT_INDEX, vocView, true);
	}

	@Override
	public VocabularyView getVocabularyView(final String aUser, final UUID aId) throws PersistException {
		return asVocabularyView(orm.get(DEFAULT_INDEX, aId.toString()));
	}

	@Override
	public void deleteVocabularyView(final String aUser, final UUID aVocabularyId) throws PersistException {
		orm.delete(DEFAULT_INDEX, aVocabularyId.toString());
	}

	private VocabularyView asVocabularyView(final DbVocabularyView aDbCardVocabularyView) {
		if (aDbCardVocabularyView == null) {
			return null;
		}
		return new PojoVocabularyView(aDbCardVocabularyView.getVocabularyId(),
				aDbCardVocabularyView.getCss(), aDbCardVocabularyView.getFrontTpl(), aDbCardVocabularyView.getBackTpl());
	}

}
