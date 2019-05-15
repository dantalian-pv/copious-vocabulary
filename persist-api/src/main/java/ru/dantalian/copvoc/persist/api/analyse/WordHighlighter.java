package ru.dantalian.copvoc.persist.api.analyse;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Language;

public interface WordHighlighter {

	String highlight(String aQuery, String aText, Language aLang) throws PersistException;

	String highlight(String aQuery, String aText, Language aLang, String aPreTag, String aPostTag) throws PersistException;

	String replace(String aText, String aQuery, String aReplacement, Language aLang) throws PersistException;

	String replace(String aText, String aQuery, String aReplacement, Language aLang, String aPreTag, String aPostTag) throws PersistException;

}
