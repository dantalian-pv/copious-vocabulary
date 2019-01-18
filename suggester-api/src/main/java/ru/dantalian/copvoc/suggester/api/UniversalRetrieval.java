package ru.dantalian.copvoc.suggester.api;

import java.net.URI;
import java.util.Map;

public interface UniversalRetrieval {

	boolean accept(URI aSource);

	Map<String, Object> retrieve(String aUser, URI aSource) throws SuggestException;

}
