package ru.dantalian.copvoc.suggester.api.model;

import java.net.URI;

public interface Suggest extends Comparable<Suggest> {

	URI getSource();

	String getGroup();

	String getKey();

	String getValue();

	String getDescription();

	Double getRank();

}
