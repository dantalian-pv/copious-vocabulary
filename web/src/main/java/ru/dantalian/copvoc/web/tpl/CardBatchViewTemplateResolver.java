package ru.dantalian.copvoc.web.tpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

import ru.dantalian.copvoc.core.managers.BatchManager;

@Service
public class CardBatchViewTemplateResolver extends StringTemplateResolver {

	@Autowired
	private BatchManager batchManager;

	@Override
	protected ITemplateResource computeTemplateResource(final IEngineConfiguration aConfiguration,
			final String aOwnerTemplate,
			final String aTemplate,
			final Map<String, Object> aTemplateResolutionAttributes) {
		return new StringTemplateResource(aTemplate);
	}

}
