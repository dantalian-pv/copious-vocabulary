package ru.dantalian.copvoc.web.controllers.rest.model;

import java.util.List;

public class DtoCard {

	private String id;

	private String batchId;

	private List<DtoCardContent> content;

	public DtoCard() {
	}

	public DtoCard(final String aId, final String aBatchId, final List<DtoCardContent> aContent) {
		id = aId;
		batchId = aBatchId;
		content = aContent;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(final String aBatchId) {
		batchId = aBatchId;
	}

	public List<DtoCardContent> getContent() {
		return content;
	}

	public void setContent(final List<DtoCardContent> aContent) {
		content = aContent;
	}

}
