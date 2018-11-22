package ru.dantalian.copvoc.ui;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopiousVocabularyUIMain {

	private static final Logger logger = LoggerFactory.getLogger(CopiousVocabularyUIMain.class);

	public static void main(final String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Copious Vocabulary");
		System.setProperty("awt.useSystemAAFontSettings", "lcd");
		System.setProperty("swing.aatext", "true");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			logger.warn("Look and Feel error", e);
		}
		initializeFontSize(100);

		final MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}

	public static void initializeFontSize(final int percent) {
		if (percent != 100) {
			final float multiplier = percent / 100.0f;
			final UIDefaults defaults = UIManager.getDefaults();
			for (final Enumeration<Object> e = defaults.keys(); e.hasMoreElements();) {
				final Object key = e.nextElement();
				final Object value = defaults.get(key);
				if (value instanceof Font) {
					final Font font = (Font) value;
					final int newSize = Math.round(font.getSize() * multiplier);
					if (value instanceof FontUIResource) {
						defaults.put(key, new FontUIResource("Dialog", font.getStyle(), newSize));
					} else {
						defaults.put(key, new Font("Dialog", font.getStyle(), newSize));
					}
				}
			}
		}
	}

}
