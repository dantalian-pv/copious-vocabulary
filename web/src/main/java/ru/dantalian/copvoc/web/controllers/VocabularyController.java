package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;

@Controller
public class VocabularyController {

	private static final String FRAME = "frame";

	@Autowired
	private PersistVocabularyManager vocManager;

	@Autowired
	private PersistVocabularyViewManager viewManager;

	@Autowired
	private PersistCardFieldManager fieldsManager;

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
		return FRAME;
	}

	@RequestMapping("/vocabularies/{id}/edit_cards")
	public String editVoc(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
		if (voc == null) {
			throw new PageNotFoundException();
		}
		final List<CardField> fields = fieldsManager.listFields(user, UUID.fromString(aId));

		aModel.addAttribute("tpl", "voc/edit_cards");
		aModel.addAttribute("voc", voc);
		aModel.addAttribute("fields", fields);
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", "Edit " + voc.getName());
		return FRAME;
	}

	@RequestMapping("/vocabularies/{id}/edit_view")
	public String editView(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
		if (voc == null) {
			throw new PageNotFoundException();
		}
		final VocabularyView view = viewManager.getVocabularyView(user, UUID.fromString(aId));

		aModel.addAttribute("tpl", "voc/edit_view");
		aModel.addAttribute("voc", voc);
		aModel.addAttribute("view", view);
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", "Edit View " + voc.getName());
		return FRAME;
	}

	@RequestMapping("/vocabularies/{id}/edit_fields")
	public String editFields(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
		if (voc == null) {
			throw new PageNotFoundException();
		}

		final List<String> fieldTypes = Arrays.asList(CardFiledType.values())
				.stream()
				.map(aItem -> aItem.name())
				.collect(Collectors.toList());

		aModel.addAttribute("tpl", "voc/edit_fields");
		aModel.addAttribute("voc", voc);
		aModel.addAttribute("fieldTypes", fieldTypes);
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", "Edit Fields " + voc.getName());
		return FRAME;
	}

}
