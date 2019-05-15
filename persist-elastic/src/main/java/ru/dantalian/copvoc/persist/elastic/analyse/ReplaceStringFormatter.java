package ru.dantalian.copvoc.persist.elastic.analyse;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class ReplaceStringFormatter implements Formatter {

	private final String replacement;

	public ReplaceStringFormatter(final String aReplacement) {
		replacement = aReplacement;
	}

	@Override
	public String highlightTerm(final String aOriginalText, final TokenGroup aTokenGroup) {
		if (aTokenGroup.getTotalScore() <= 0) {
      return aOriginalText;
    }
		return replacement;
	}

}
