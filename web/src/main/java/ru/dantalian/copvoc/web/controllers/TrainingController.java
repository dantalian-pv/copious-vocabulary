package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.dantalian.copvoc.core.utils.StatsUtils;
import ru.dantalian.copvoc.persist.api.EmptyResultPersistException;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistTrainingManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.Training;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.api.query.QueryBuilder;
import ru.dantalian.copvoc.persist.api.query.QueryFactory;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoTrainingStats;
import ru.dantalian.copvoc.web.utils.DtoCodec;

@Controller
public class TrainingController {

	@Autowired
	private PersistTrainingManager trainingManager;

	@Autowired
	private PersistVocabularyViewManager viewPersist;

	@Autowired
	private PersistVocabularyManager vocPersist;

	@RequestMapping("/training/{voc_id}")
	public String create(@PathVariable("voc_id") final String aVocabularyId, final Principal aPrincipal,
			final Model aModel) throws PersistException {
		final String user = aPrincipal.getName();
		final UUID vocId = UUID.fromString(aVocabularyId);
		final VocabularyView vocView = viewPersist.getVocabularyView(user, vocId);
		if (vocView == null) {
			throw new PageNotFoundException();
		}

		final Vocabulary voc = vocPersist.getVocabulary(user, vocId);
		if (voc == null) {
			throw new PageNotFoundException();
		}

		aModel.addAttribute("tpl", "training");
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", voc.getName());
		aModel.addAttribute("voc", DtoCodec.asDtoVocabulary(voc));
		aModel.addAttribute("view", DtoCodec.asDtoView(vocView));

		Training training = null;
		try {
			final QueryBuilder query = QueryFactory.newCardsQuery();
			query.setVocabularyId(vocId);
			query.where(QueryFactory.eq("finished", false, false));
			final List<Training> trainings = trainingManager.queryTrainings(user, query.build());
			if (!trainings.isEmpty()) {
				training = trainings.get(0);
			} else {
				final Map<String, CardStat> stats = StatsUtils.defaultStats();
				training = trainingManager.createTraining(
						user, vocId, Optional.empty(), stats);
			}
		} catch (final EmptyResultPersistException e) {
			aModel.addAttribute("error", "No cards found.");
		}
		if (training != null) {
			final UUID firstCard = trainingManager.firstCard(user, training.getId());
			aModel.addAttribute("training", DtoCodec.asDtoTrainingStats(training));
			aModel.addAttribute("firstCardId", firstCard.toString());
		} else {
			aModel.addAttribute("training", new DtoTrainingStats());
			aModel.addAttribute("firstCardId", null);
		}
		return "frame";
	}

	@RequestMapping("/training/{training_id}/result")
	public String result(@PathVariable("training_id") final String aTrainingId, final Principal aPrincipal,
			final Model aModel) throws PersistException {
		final String user = aPrincipal.getName();
		final UUID trainingId = UUID.fromString(aTrainingId);

		final Training training = trainingManager.getTraining(user, trainingId);
		if (training == null) {
			throw new PageNotFoundException();
		}
		final Vocabulary voc = vocPersist.getVocabulary(user, training.getVocabularyId());
		if (voc == null) {
			throw new PageNotFoundException();
		}
		trainingManager.finishTraining(user, trainingId);

		aModel.addAttribute("tpl", "training_result");
		aModel.addAttribute("top_menu", true);
		aModel.addAttribute("title", voc.getName());
		aModel.addAttribute("training", DtoCodec.asDtoTrainingStats(training));
		aModel.addAttribute("voc", DtoCodec.asDtoVocabulary(voc));

		return "frame";
	}

}
