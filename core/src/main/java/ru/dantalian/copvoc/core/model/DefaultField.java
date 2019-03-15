package ru.dantalian.copvoc.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public class DefaultField {

	@JsonProperty("name")
	private String name;

	@JsonProperty("type")
	private CardFiledType type;

	@JsonProperty("order")
	private Integer order;

	@JsonProperty("system")
	private boolean system;

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	public CardFiledType getType() {
		return type;
	}

	public void setType(final CardFiledType aType) {
		type = aType;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(final Integer aOrder) {
		order = aOrder;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(final boolean aSystem) {
		system = aSystem;
	}

}
