package ru.dantalian.copvoc.suggester.retrieval;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.UniversalRetrieval;

@Component("main")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CombinedResourceRetrieval implements UniversalRetrieval {

	@Autowired
	private List<UniversalRetrieval> retrievals;

	@Override
	public Map<String, Object> retrieve(final String aUser, final URI aSource) throws SuggestException {
		for (final UniversalRetrieval retrieval: retrievals) {
			if (retrieval.accept(aSource)) {
				return retrieval.retrieve(aUser, aSource);
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public boolean accept(final URI aSource) {
		return false;
	}

}
