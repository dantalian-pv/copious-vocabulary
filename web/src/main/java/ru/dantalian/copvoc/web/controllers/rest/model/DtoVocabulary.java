package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoVocabulary {

	private String id;

	private String name;

	private String description;

	private String sourceId;

	private String targetId;

	private String sourceName;

	private String targetName;

	public DtoVocabulary() {
	}

	public DtoVocabulary(final String aId, final String aName, final String aDescription,
			final String aSourceId, final String aSourceName, final String aTargetId, final String aTargetName) {
		id = aId;
		name = aName;
		description = aDescription;
		sourceId = aSourceId;
		sourceName = aSourceName;
		targetId = aTargetId;
		targetName = aTargetName;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
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

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(final String aSourceId) {
		sourceId = aSourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(final String aTargetId) {
		targetId = aTargetId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(final String aSourceName) {
		sourceName = aSourceName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(final String aTargetName) {
		targetName = aTargetName;
	}

}
