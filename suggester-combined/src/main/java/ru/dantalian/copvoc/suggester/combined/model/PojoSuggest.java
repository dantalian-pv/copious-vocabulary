package ru.dantalian.copvoc.suggester.combined.model;

import java.net.URI;

import ru.dantalian.copvoc.suggester.api.model.Suggest;

public class PojoSuggest implements Suggest {

	private URI source;

	private String group;

	private String key;

	private String value;

	private String description;

	private Double rank;

	public PojoSuggest() {
	}

	public PojoSuggest(final URI aSource, final String aGroup, final String aKey,
			final String aValue, final String aDescription, final Double aRank) {
		source = aSource;
		group = aGroup;
		key = aKey;
		value = aValue;
		description = aDescription;
		rank = aRank;
	}

	@Override
	public URI getSource() {
		return source;
	}

	public void setSource(final URI aSource) {
		source = aSource;
	}

	@Override
	public String getGroup() {
		return group;
	}

	public void setGroup(final String aGroup) {
		group = aGroup;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(final String aDescription) {
		description = aDescription;
	}

	@Override
	public Double getRank() {
		return rank;
	}

	@Override
	public int compareTo(final Suggest aO) {
		if (rank == null) {
			return 1;
		}
		if (aO.getRank() == null) {
			return -1;
		}
		return Double.compare(rank.doubleValue(), aO.getRank().doubleValue());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PojoSuggest)) {
			return false;
		}
		final PojoSuggest other = (PojoSuggest) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!source.equals(other.source)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PojoSuggest [source=" + source + ", group=" + group + ", key=" + key
				+ ", value=" + value + ", description=" + description + ", rank=" + rank + "]";
	}

}
