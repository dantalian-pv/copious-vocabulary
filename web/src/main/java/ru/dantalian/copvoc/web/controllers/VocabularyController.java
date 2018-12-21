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
import ru.dantalian.copvoc.persist.api.model.Vocabulary;

@Controller
public class VocabularyController {

	@Autowired
	private PersistVocabularyManager batchManager;

	@RequestMapping("/vocabularies/{id}")
	public String batch(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary batch = batchManager.getVocabulary(user, UUID.fromString(aId));
		if (batch == null) {
			throw new PageNotFoundException();
		}

		aModel.addAttribute("tpl", "batch");
		aModel.addAttribute("batch", batch);
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", batch.getName());
		return "frame";
	}

	@RequestMapping("/batches/{id}/edit_cards")
	public String editBatch(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary batch = batchManager.getVocabulary(user, UUID.fromString(aId));
		if (batch == null) {
			throw new PageNotFoundException();
		}

		aModel.addAttribute("tpl", "batch/edit_cards");
		aModel.addAttribute("batch", batch);
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", "Edit " + batch.getName());
		return "frame";
	}

}
