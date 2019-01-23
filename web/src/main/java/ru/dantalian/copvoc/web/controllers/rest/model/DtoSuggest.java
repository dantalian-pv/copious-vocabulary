package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoSuggest {

	private String source;

	private String group;

	private String key;

	private String value;

	private String description;

	private Double rank;

	public DtoSuggest() {
	}

	public DtoSuggest(final String aSource, final String aGroup, final String aKey, final String aValue,
			final String aDescription, final Double aRank) {
		source = aSource;
		group = aGroup;
		key = aKey;
		value = aValue;
		description = aDescription;
		rank = aRank;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String aSource) {
		source = aSource;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(final String aGroup) {
		group = aGroup;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String aKey) {
		key = aKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String aValue) {
		value = aValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String aDescription) {
		description = aDescription;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(final Double aRank) {
		rank = aRank;
	}

	@Override
	public String toString() {
		return "DtoSuggest [key=" + key + ", value=" + value + ", rank=" + rank + "]";
	}

}
