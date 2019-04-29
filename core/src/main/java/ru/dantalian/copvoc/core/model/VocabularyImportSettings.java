package ru.dantalian.copvoc.core.model;

public class VocabularyImportSettings {

	private final boolean addFields;

	private final boolean skipIncompatibleFields;

	private final boolean overwriteView;

	private final boolean overwriteCards;

	public VocabularyImportSettings(final boolean aAddFields, final boolean aSkipIncompatibleFields,
			final boolean aOverwriteView, final boolean aOverwriteCards) {
		addFields = aAddFields;
		skipIncompatibleFields = aSkipIncompatibleFields;
		overwriteView = aOverwriteView;
		overwriteCards = aOverwriteCards;
	}

	public boolean isAddFields() {
		return addFields;
	}

	public boolean isSkipIncompatibleFields() {
		return skipIncompatibleFields;
	}

	public boolean isOverwriteView() {
		return overwriteView;
	}

	public boolean isOverwriteCards() {
		return overwriteCards;
	}

	@Override
	public String toString() {
		return "VocabularyImportSettings [addFields=" + addFields
				+ ", skipIncompatibleFields=" + skipIncompatibleFields
				+ ", overwriteView=" + overwriteView + ", overwriteCards=" + overwriteCards + "]";
	}

}
