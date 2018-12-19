package ru.dantalian.copvoc.persist.impl.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardBatchView;

public class PojoCardBatchView implements CardBatchView {

	private UUID id;

	private UUID batchId;

	private String css;

	private String front;

	private String back;

	public PojoCardBatchView() {
	}

	public PojoCardBatchView(final UUID aId, final UUID aBatchId, final String aCss,
			final String aFront, final String aBack) {
		id = aId;
		batchId = aBatchId;
		css = aCss;
		front = aFront;
		back = aBack;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	@Override
	public UUID getBatchId() {
		return batchId;
	}

	public void setBatchId(final UUID aBatchId) {
		batchId = aBatchId;
	}

	@Override
	public String getCss() {
		return css;
	}

	public void setCss(final String aCss) {
		css = aCss;
	}

	@Override
	public String getFront() {
		return front;
	}

	public void setFront(final String aFront) {
		front = aFront;
	}

	@Override
	public String getBack() {
		return back;
	}

	public void setBack(final String aBack) {
		back = aBack;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (!(obj instanceof PojoCardBatchView)) {
			return false;
		}
		final PojoCardBatchView other = (PojoCardBatchView) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PojoCardBatchView [id=" + id + ", batchId=" + batchId + "]";
	}

}
