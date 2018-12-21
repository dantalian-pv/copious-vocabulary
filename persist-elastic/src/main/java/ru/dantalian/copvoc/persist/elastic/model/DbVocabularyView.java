package ru.dantalian.copvoc.persist.elastic.model;

import java.util.UUID;

public class DbVocabularyView {

	private UUID vocabularyId;

	private String css;

	private String frontTpl;

	private String backTpl;

	public DbVocabularyView() {
	}

	public DbVocabularyView(final UUID aVocabularyId, final String aCss,
			final String aFrontTpl, final String aBackTpl) {
		vocabularyId = aVocabularyId;
		css = aCss;
		frontTpl = aFrontTpl;
		backTpl = aBackTpl;
	}

	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
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
		result = prime * result + ((vocabularyId == null) ? 0 : vocabularyId.hashCode());
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
		if (!(obj instanceof DbVocabularyView)) {
			return false;
		}
		final DbVocabularyView other = (DbVocabularyView) obj;
		if (vocabularyId == null) {
			if (other.vocabularyId != null) {
				return false;
			}
		} else if (!vocabularyId.equals(other.vocabularyId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DbVocabularyView [vocabularyId=" + vocabularyId + "]";
	}

}
