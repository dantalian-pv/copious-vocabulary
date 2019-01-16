package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoSuggest {

	private String source;

	private String key;

	private String value;

	private Double rank;

	public DtoSuggest() {
	}

	public DtoSuggest(final String aSource, final String aKey, final String aValue, final Double aRank) {
		source = aSource;
		key = aKey;
		value = aValue;
		rank = aRank;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String aSource) {
		source = aSource;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public Double getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return "DtoSuggest [key=" + key + ", value=" + value + ", rank=" + rank + "]";
	}

}
