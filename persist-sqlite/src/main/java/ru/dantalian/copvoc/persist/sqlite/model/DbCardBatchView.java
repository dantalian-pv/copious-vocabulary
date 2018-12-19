package ru.dantalian.copvoc.persist.sqlite.model;

import java.util.UUID;

public class DbCardBatchView {

	private UUID id;

	private UUID batchId;

	private String css;

	private String frontTpl;

	private String backTpl;

	public DbCardBatchView() {
	}

	public DbCardBatchView(final UUID aId, final UUID aBatchId, final String aCss,
			final String aFrontTpl, final String aBackTpl) {
		id = aId;
		batchId = aBatchId;
		css = aCss;
		frontTpl = aFrontTpl;
		backTpl = aBackTpl;
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	public UUID getBatchId() {
		return batchId;
	}

	public void setBatchId(final UUID aBatchId) {
		batchId = aBatchId;
	}

	public String getCss() {
		return css;
	}

	public void setCss(final String aCss) {
		css = aCss;
	}

	public String getFrontTpl() {
		return frontTpl;
	}

	public void setFrontTpl(final String aFrontTpl) {
		frontTpl = aFrontTpl;
	}

	public String getBackTpl() {
		return backTpl;
	}

	public void setBackTpl(final String aBackTpl) {
		backTpl = aBackTpl;
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
		if (!(obj instanceof DbCardBatchView)) {
			return false;
		}
		final DbCardBatchView other = (DbCardBatchView) obj;
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
		return "DbCardBatchView [id=" + id + ", batchId=" + batchId + "]";
	}

}
