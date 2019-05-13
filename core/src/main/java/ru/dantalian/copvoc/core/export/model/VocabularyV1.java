package ru.dantalian.copvoc.core.export.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VocabularyV1 {

	private String name;

	private String description;

	private String source;

	private String target;

	public VocabularyV1() {
	}

	public VocabularyV1(final String aName, final String aDescription, final String aSource, final String aTarget) {
		name = aName;
		description = aDescription;
		source = aSource;
		target = aTarget;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String aDescription) {
		description = aDescription;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String aSource) {
		source = aSource;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(final String aTarget) {
		target = aTarget;
	}

}
