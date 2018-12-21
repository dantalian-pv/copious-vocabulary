package ru.dantalian.copvoc.web.tpl;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.web.controllers.PageNotFoundException;

@Service
public class VocabularyViewTemplateResolver extends StringTemplateResolver {

	@Autowired
	private PersistVocabularyViewManager vocViewPersist;

	@Override
	protected ITemplateResource computeTemplateResource(final IEngineConfiguration aConfiguration,
			final String aOwnerTemplate,
			final String aTemplate,
			final Map<String, Object> aTemplateResolutionAttributes) {
		// No way to get mvc view model here, have to encode batchView id in template name
		final String viewId = aTemplate.substring(aTemplate.indexOf("/") + 1);
		final String tplName = aTemplate.substring(0, aTemplate.indexOf("/"));
		try {
			final VocabularyView view = vocViewPersist.getVocabularyView(null, UUID.fromString(viewId));
			if (view == null) {
				throw new PageNotFoundException();
			}
			switch (tplName) {
				case "front":
					return new StringTemplateResource(view.getFront());
				case "back":
					return new StringTemplateResource(view.getBack());
				default:
					throw new PageNotFoundException();
			}
		} catch (final PersistException e) {
			throw new RuntimeException("Failed to get batch view for " + viewId);
		}
	}

}
