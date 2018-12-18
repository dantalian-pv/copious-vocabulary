package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.dantalian.copvoc.core.managers.BatchManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardBatch;

@Controller
public class CardBatchController {

	@Autowired
	private BatchManager batchManager;

	@RequestMapping("/batches/{id}")
	String index(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final CardBatch batch = batchManager.getBatch(user, UUID.fromString(aId));
		if (batch == null) {
			throw new PageNotFoundException();
		}

		aModel.addAttribute("tpl", "batch");
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", batch.getName());
		return "frame";
	}

}
