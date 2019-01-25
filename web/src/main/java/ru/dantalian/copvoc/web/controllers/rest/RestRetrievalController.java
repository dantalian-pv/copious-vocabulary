package ru.dantalian.copvoc.web.controllers.rest;

import java.net.URI;
import java.security.Principal;
import java.util.Base64;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.UniversalRetrieval;

@RestController
@RequestMapping(value = "/v1/api/retrieval", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestRetrievalController {

	@Resource(name = "main")
	private UniversalRetrieval retrieval;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> retrieve(final Principal aPrincipal,
			@RequestParam(value = "uri", required = true) final String aUri)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final String decodedUri = new String(Base64.getDecoder().decode(aUri));
			final URI uri = URI.create(decodedUri);
			return retrieval.retrieve(user, uri);
		} catch (final SuggestException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

}
