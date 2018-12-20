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

	@RequestMapping("/batch_views/{batch_id}")
	public String view(@PathVariable("batch_id") final String aBatchId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final CardBatchView batchView = batchManager.getBatchViewByBatchId(UUID.fromString(aBatchId));
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
		aModel.addAttribute("batch", batch);
		aModel.addAttribute("batchView", batchView);
		return "frame";
	}

}
