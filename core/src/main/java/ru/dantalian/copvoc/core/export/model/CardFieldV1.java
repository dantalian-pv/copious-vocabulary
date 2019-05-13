package ru.dantalian.copvoc.core.export.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.dantalian.copvoc.persist.api.model.CardFiledType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardFieldV1 {

	private String name;

	private CardFiledType type;

	private Integer order;

	boolean system;

	public CardFieldV1() {
	}

	public CardFieldV1(final String aName, final CardFiledType aType, final Integer aOrder, final boolean aSystem) {
		name = aName;
		type = aType;
		order = aOrder;
		system = aSystem;
	}

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
