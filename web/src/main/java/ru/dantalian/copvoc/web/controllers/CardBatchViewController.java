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
import ru.dantalian.copvoc.persist.api.model.CardBatchView;

@Controller
public class CardBatchViewController {

	@Autowired
	private BatchManager batchManager;

	@RequestMapping("/batch_views/{id}")
	public String view(@PathVariable("id") final String aId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final CardBatchView batchView = batchManager.getBatchView(UUID.fromString(aId));
		if (batchView == null) {
			throw new PageNotFoundException();
		}
		final String user = aPrincipal.getName();
		final CardBatch batch = batchManager.getBatch(user, batchView.getBatchId());
		if (batch == null) {
			throw new PageNotFoundException();
		}
		aModel.addAttribute("tpl", "batch_view");
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", batch.getName());
		return "frame";
	}

}
