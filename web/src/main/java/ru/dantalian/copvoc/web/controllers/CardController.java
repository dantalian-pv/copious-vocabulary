package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.impl.query.QueryFactory;
import ru.dantalian.copvoc.web.utils.DtoCodec;

@Controller
public class CardController {

	@Autowired
	private PersistVocabularyManager vocPersist;

	@Autowired
	private PersistVocabularyViewManager viewPersist;

	@Autowired
	private PersistCardManager cardPersist;

	@RequestMapping("/cards/{voc_id}")
	public String view(@PathVariable("voc_id") final String aVocabularyId, final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final VocabularyView vocView = viewPersist.getVocabularyView(user, UUID.fromString(aVocabularyId));
		if (vocView == null) {
			throw new PageNotFoundException();
		}
		final Vocabulary voc = vocPersist.getVocabulary(user, UUID.fromString(aVocabularyId));
		if (voc == null) {
			throw new PageNotFoundException();
		}
		final List<Card> cards = cardPersist.queryCards(user, QueryFactory.newCardsQuery()
				.setVocabularyId(UUID.fromString(aVocabularyId)).build());
		if (cards.isEmpty()) {
			throw new PageNotFoundException("No cards in vocabulary");
		}


		return "redirect:/cards/" + voc.getId().toString() + "/" + cards.get(0).getId().toString();
	}

	@RequestMapping("/cards/{voc_id}/{id}")
	public String view(@PathVariable("voc_id") final String aVocabularyId,
			@PathVariable("id") final String aId,
			final Principal aPrincipal, final Model aModel)
			throws PersistException {
		final String user = aPrincipal.getName();
		final VocabularyView vocView = viewPersist.getVocabularyView(user, UUID.fromString(aVocabularyId));
		if (vocView == null) {
			throw new PageNotFoundException();
		}
		final Vocabulary voc = vocPersist.getVocabulary(user, UUID.fromString(aVocabularyId));
		if (voc == null) {
			throw new PageNotFoundException();
		}
		final List<Card> cards = cardPersist.queryCards(user, QueryFactory.newCardsQuery()
				.setVocabularyId(UUID.fromString(aVocabularyId)).build());
		final Card card = cardPersist.getCard(user, UUID.fromString(aVocabularyId), UUID.fromString(aId));
		if (card == null) {
			throw new PageNotFoundException();
		}
		Card nextCard = null;
		final Iterator<Card> itr = cards.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			idx++;
			if (itr.next().getId().equals(card.getId())) {
				nextCard = itr.hasNext() ? itr.next() : null;
				break;
			}
		}

		aModel.addAttribute("tpl", "training");
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", voc.getName());
		aModel.addAttribute("voc", DtoCodec.asDtoVocabulary(voc));
		aModel.addAttribute("view", DtoCodec.asDtoView(vocView));
		aModel.addAttribute("card", DtoCodec.asDtoCard(card));
		aModel.addAttribute("cards_size", cards.size());
		aModel.addAttribute("cards_idx", idx);
		aModel.addAttribute("nextCard", DtoCodec.asDtoCard(nextCard));
		return "frame";
	}

}
