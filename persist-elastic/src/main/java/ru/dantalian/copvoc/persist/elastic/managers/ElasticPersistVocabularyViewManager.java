package ru.dantalian.copvoc.persist.elastic.managers;

import java.util.UUID;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.elastic.model.DbVocabularyView;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabularyView;

@Service
public class ElasticPersistVocabularyViewManager extends AbstractPersistManager<DbVocabularyView>
	implements PersistVocabularyViewManager {

	private static final String DEFAULT_INDEX = "views";

	@Autowired
	public ElasticPersistVocabularyViewManager(final RestHighLevelClient aClient) {
		super(aClient, DbVocabularyView.class);
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
	public VocabularyView createVocabularyView(final String aUser, final UUID aVocabularyId, final String aCss, final String aFrontTpl,
			final String aBackTpl) throws PersistException {
		final DbVocabularyView vocView = new DbVocabularyView(aVocabularyId, aCss, aFrontTpl, aBackTpl);
		add(DEFAULT_INDEX, vocView, true);
		return asVocabularyView(vocView);
	}

	@Override
	public void updateVocabularyView(final String aUser, final UUID aId, final String aCss, final String aFrontTpl, final String aBackTpl)
			throws PersistException {
		final DbVocabularyView vocView = new DbVocabularyView(aId, aCss, aFrontTpl, aBackTpl);
		update(DEFAULT_INDEX, vocView, true);
	}

	@Override
	public VocabularyView getVocabularyView(final String aUser, final UUID aId) throws PersistException {
		return asVocabularyView(get(DEFAULT_INDEX, aId.toString()));
	}

	private VocabularyView asVocabularyView(final DbVocabularyView aDbCardVocabularyView) {
		if (aDbCardVocabularyView == null) {
			return null;
		}
		return new PojoVocabularyView(aDbCardVocabularyView.getVocabularyId(),
				aDbCardVocabularyView.getCss(), aDbCardVocabularyView.getFrontTpl(), aDbCardVocabularyView.getBackTpl());
	}

}