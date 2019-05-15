package ru.dantalian.copvoc.persist.elastic.analyse;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import ru.dantalian.copvoc.persist.impl.model.PojoLanguage;

public class StandardWordHighlighterTest {

	@Test
	public void shouldHighlightOneWord() throws Exception {
		final StandardWordHighlighter highlighter = new StandardWordHighlighter();
		final PojoLanguage lang = new PojoLanguage("en", "EN", "", "");
		final String highlighted = highlighter.highlight("test", "some text for test", lang);
		Assert.assertThat(highlighted, CoreMatchers.containsString("[[test]]"));
	}

	@Test
	public void shouldHighlightOneRussianWord() throws Exception {
		final StandardWordHighlighter highlighter = new StandardWordHighlighter();
		final PojoLanguage lang = new PojoLanguage("ru", "RU", "", "");
		final String highlighted = highlighter.highlight("проверка", "текст для проверки подсветки", lang);
		Assert.assertThat(highlighted, CoreMatchers.containsString("[[проверки]]"));
	}

	@Test
	public void shouldHighlightOneJapaneseWord() throws Exception {
		final StandardWordHighlighter highlighter = new StandardWordHighlighter();
		final PojoLanguage lang = new PojoLanguage("jp", "JP", "", "");
		final String highlighted = highlighter.highlight("テスト", "次の月曜に私達は英語のテストを受ける。",  lang);
		Assert.assertThat(highlighted, CoreMatchers.containsString("[[テスト]]"));
	}

	@Test
	public void shouldReplaceOneWord() throws Exception {
		final StandardWordHighlighter highlighter = new StandardWordHighlighter();
		final PojoLanguage lang = new PojoLanguage("en", "EN", "", "");
		final String highlighted = highlighter.replace("some text for test", "test", "тест", lang);
		Assert.assertThat(highlighted, CoreMatchers.equalTo("some text for [[тест]]"));
	}

	@Test
	public void shouldReplaceOneRussianWord() throws Exception {
		final StandardWordHighlighter highlighter = new StandardWordHighlighter();
		final PojoLanguage lang = new PojoLanguage("ru", "RU", "", "");
		final String highlighted = highlighter.replace("текст для проверки подсветки", "проверка", "check", lang);
		Assert.assertThat(highlighted, CoreMatchers.equalTo("текст для [[check]] подсветки"));
	}

	@Test
	public void shouldReplaceOneJapaneseWord() throws Exception {
		final StandardWordHighlighter highlighter = new StandardWordHighlighter();
		final PojoLanguage lang = new PojoLanguage("jp", "JP", "", "");
		final String highlighted = highlighter.replace("次の月曜に私達は英語のテストを受ける。", "テスト", "test", lang);
		Assert.assertThat(highlighted, CoreMatchers.equalTo("次の月曜に私達は英語の[[test]]を受ける。"));
	}

}
