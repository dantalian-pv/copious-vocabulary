package ru.dantalian.copvoc.persist.elastic.analyse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryTermScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.elasticsearch.Version;
import org.elasticsearch.indices.analysis.PreBuiltAnalyzers;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.analyse.WordHighlighter;
import ru.dantalian.copvoc.persist.api.model.Language;

@Service
public class StandardWordHighlighter implements WordHighlighter {

	public static final String POST_TAG = "]]";

	public static final String PRE_TAG = "[[";

	private final Map<String, Analyzer> analyzers = new ConcurrentHashMap<>();

	@Override
	public String highlight(final String aQuery, final String aText, final Language aLang) throws PersistException {
		return highlight(aQuery, aText, aLang, PRE_TAG, POST_TAG);
	}

	@Override
	public String highlight(final String aQuery, final String aText, final Language aLang, final String aPreTag, final String aPostTag) throws PersistException {
		try {
			final Analyzer analyzer = getAnalyzer(aLang);
			final QueryParser qp = new QueryParser("content", analyzer);
			final Query query = qp.parse(aQuery);

			final SimpleHTMLFormatter formatter = new SimpleHTMLFormatter(aPreTag, aPostTag);
			final QueryTermScorer scorer = new QueryTermScorer(query);
			final Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setTextFragmenter(new NullFragmenter());
			final String highlighted = highlighter.getBestFragment(
					analyzer.tokenStream("content", aText), aText);

			return highlighted;
		} catch (final ParseException | IOException | InvalidTokenOffsetsException e) {
			throw new PersistException("Failed to highlight text", e);
		}
	}

	@Override
	public String replace(final String aText, final String aQuery, final String aReplacement, final Language aLang)
			throws PersistException {
		return replace(aText, aQuery, aReplacement, aLang, PRE_TAG, POST_TAG);
	}

	@Override
	public String replace(final String aText, final String aQuery, final String aReplacement,
			final Language aLang, final String aPreTag, final String aPostTag) throws PersistException {
		try {
			final Analyzer analyzer = getAnalyzer(aLang);
			final QueryParser qp = new QueryParser("content", analyzer);
			final Query query = qp.parse(aQuery);

			final ReplaceStringFormatter formatter = new ReplaceStringFormatter(aPreTag + aReplacement + aPostTag);
			final QueryTermScorer scorer = new QueryTermScorer(query);
			final Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setTextFragmenter(new NullFragmenter());
			final String highlighted = highlighter.getBestFragment(
					analyzer.tokenStream("content", aText), aText);

			return highlighted;
		} catch (final ParseException | IOException | InvalidTokenOffsetsException e) {
			throw new PersistException("Failed to highlight text", e);
		}
	}

	private Analyzer getAnalyzer(final Language aLang) {
		switch (aLang.getName()) {
			case "jp":
				return checkCacheAndReturn(aLang.getName(), () -> new JapaneseAnalyzer()) ;
			case "ru":
				return checkCacheAndReturn(aLang.getName(), () -> new RussianAnalyzer());
			default:
				return checkCacheAndReturn(aLang.getName(), () -> PreBuiltAnalyzers.CLASSIC.getAnalyzer(Version.CURRENT));
		}
	}

	private Analyzer checkCacheAndReturn(final String aName, final Supplier<Analyzer> aAnalyzer) {
		Analyzer analyzer = analyzers.get(aName);
		if (analyzer == null) {
			analyzer = aAnalyzer.get();
			analyzers.put(aName, analyzer);
		}
		return analyzer;
	}

}
