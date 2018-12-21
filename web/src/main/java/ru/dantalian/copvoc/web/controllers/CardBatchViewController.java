package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;

@Controller
public class CardBatchViewController {

	@Autowired
	private PersistVocabularyManager batchPersist;

	@Autowired
	private PersistVocabularyViewManager batchViewPersist;

	@RequestMapping("/vocabulary_views/{batch_id}")
	public String view(@PathVariable("batch_id") final String aVocabularyId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final VocabularyView vocView = batchViewPersist.getVocabularyView(user, UUID.fromString(aVocabularyId));
		if (vocView == null) {
			throw new PageNotFoundException();
		}
		final Vocabulary voc = batchPersist.getVocabulary(user, UUID.fromString(aVocabularyId));
		if (voc == null) {
			throw new PageNotFoundException();
		}
		aModel.addAttribute("tpl", "batch_view");
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", voc.getName());
		aModel.addAttribute("batch", voc);
		aModel.addAttribute("batchView", vocView);
		return "frame";
	}

}
