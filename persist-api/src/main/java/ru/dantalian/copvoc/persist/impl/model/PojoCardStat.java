package ru.dantalian.copvoc.persist.impl.model;

import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.CardStatType;
import ru.dantalian.copvoc.persist.api.utils.Validator;

public class PojoCardStat implements CardStat {

	private final String name;

	private final Object value;

	private final CardStatType type;

	public PojoCardStat(final String aName, final Object aValue, final CardStatType aType) {
		name = aName;
		value = aValue;
		type = aType;
		validate(aName, aValue, aType);
	}

	private void validate(final String aName, final Object aValue, final CardStatType aType) {
		Validator.checkNotNull(aName, "name cannot be null");

		if (aValue != null) {
			if ((Long.class.isAssignableFrom(aValue.getClass()) && aType != CardStatType.LONG)
					|| (Double.class.isAssignableFrom(aValue.getClass()) && aType != CardStatType.DOUBLE)) {
				throw new IllegalArgumentException("Wrong stat type " + aType + " for " + aValue.getClass());
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) value;
	}

	@Override
	public CardStatType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (!(obj instanceof PojoCardStat)) {
			return false;
		}
		final PojoCardStat other = (PojoCardStat) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type != other.type) {
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
		return "PojoCardStat [name=" + name + ", value=" + value + ", type=" + type + "]";
	}

}
