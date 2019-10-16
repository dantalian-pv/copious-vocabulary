package ru.dantalian.copvoc.web.controllers.rest.model;

import java.util.Objects;
import java.util.Optional;

import ru.dantalian.copvoc.core.utils.MessageUtils;
import ru.dantalian.copvoc.persist.api.model.CardStatType;

public class DtoCardStat<T> {

	private final String name;

	private final CardStatType type;

	private final T value;

	public DtoCardStat(final String aName, final CardStatType aType, final T aValue) {
		name = Objects.requireNonNull(aName);
		type = Objects.requireNonNull(aType);
		value = Optional.ofNullable(aValue).orElseGet(() -> {
			switch (aType) {
				case DATE: {
					return null;
				}
				case DOUBLE: {
					return (T) Double.valueOf(0.0d);
				}
				case LONG: {
					return (T) Long.valueOf(0L);
				}
				default: {
					throw new IllegalArgumentException(MessageUtils.message("Unknown value type {}", aType));
				}
			}
		});
	}

	public String getName() {
		return name;
	}

	public CardStatType getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
		result = prime * result + (value == null ? 0 : value.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DtoCardStat other = (DtoCardStat) obj;
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
		return "DtoCardStat [name=" + name + ", type=" + type + ", value=" + value + "]";
	}

}
