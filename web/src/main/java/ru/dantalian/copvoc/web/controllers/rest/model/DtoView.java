package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoView {

	private String id;

	private String css;

	private String front;

	private String back;

	public DtoView() {
	}

	public DtoView(final String aId, final String aCss, final String aFront, final String aBack) {
		id = aId;
		css = aCss;
		front = aFront;
		back = aBack;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getCss() {
		return css;
	}

	public void setCss(final String aCss) {
		css = aCss;
	}

	public String getFront() {
		return front;
	}

	public void setFront(final String aFront) {
		front = aFront;
	}

	public String getBack() {
		return back;
	}

	public void setBack(final String aBack) {
		back = aBack;
	}

}
