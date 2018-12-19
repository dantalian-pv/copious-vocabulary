package ru.dantalian.copvoc.web.tpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

public class MixedDelegateTemplateResolver extends SpringResourceTemplateResolver {

	@Autowired
	private CardBatchViewTemplateResolver cardViewTemplateResolver;

	@Override
	protected ITemplateResource computeTemplateResource(final IEngineConfiguration aConfiguration,
			final String aOwnerTemplate, final String aTemplate,
			final String aResourceName, final String aCharacterEncoding,
			final Map<String, Object> aTemplateResolutionAttributes) {
		if (aTemplate.equals("front") || aTemplate.equals("back")) {
			return cardViewTemplateResolver.computeTemplateResource(aConfiguration,
					aOwnerTemplate, aTemplate, aTemplateResolutionAttributes);
		}
		return super.computeTemplateResource(aConfiguration, aOwnerTemplate, aTemplate, aResourceName,
				aCharacterEncoding, aTemplateResolutionAttributes);
	}

}
