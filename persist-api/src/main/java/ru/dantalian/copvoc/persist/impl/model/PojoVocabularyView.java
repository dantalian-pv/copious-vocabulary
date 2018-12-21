package ru.dantalian.copvoc.persist.impl.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.VocabularyView;

public class PojoVocabularyView implements VocabularyView {

	private UUID vocabularyId;

	private String css;

	private String front;

	private String back;

	public PojoVocabularyView() {
	}

	public PojoVocabularyView(final UUID aVocabularyId, final String aCss,
			final String aFront, final String aBack) {
		vocabularyId = aVocabularyId;
		css = aCss;
		front = aFront;
		back = aBack;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
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
		if (!(obj instanceof PojoVocabularyView)) {
			return false;
		}
		final PojoVocabularyView other = (PojoVocabularyView) obj;
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
		return "PojoVocabularyView [vocabularyId=" + vocabularyId + "]";
	}

}
