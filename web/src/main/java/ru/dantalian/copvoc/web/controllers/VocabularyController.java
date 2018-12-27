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
	private PersistVocabularyManager vocManager;

	@RequestMapping("/vocabularies/{id}")
	public String voc(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
		if (voc == null) {
			throw new PageNotFoundException();
		}

		aModel.addAttribute("tpl", "voc");
		aModel.addAttribute("voc", voc);
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", voc.getName());
		return "frame";
	}

	@RequestMapping("/vocabularies/{id}/edit_cards")
	public String editVoc(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
		if (voc == null) {
			throw new PageNotFoundException();
		}

		aModel.addAttribute("tpl", "voc/edit_cards");
		aModel.addAttribute("voc", voc);
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", "Edit " + voc.getName());
		return "frame";
	}

}
