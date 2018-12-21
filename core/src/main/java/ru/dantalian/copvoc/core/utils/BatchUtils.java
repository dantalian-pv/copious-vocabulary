package ru.dantalian.copvoc.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabularyView;

@Service
public class BatchUtils {

	public VocabularyView getDefaultView(final UUID aBatchId) throws CoreException {
		try (InputStream cssStream = this.getClass().getClassLoader()
				.getResourceAsStream(CoreConstants.DEFAULT_CARD_BATCH_VIEW_CSS);
				InputStream frontStream = this.getClass().getClassLoader()
						.getResourceAsStream(CoreConstants.DEFAULT_CARD_BATCH_VIEW_FRONT);
				InputStream backStream = this.getClass().getClassLoader()
						.getResourceAsStream(CoreConstants.DEFAULT_CARD_BATCH_VIEW_BACK)) {
			final String css = new BufferedReader(new InputStreamReader(cssStream))
				  .lines().collect(Collectors.joining("\n"));
			final String front = new BufferedReader(new InputStreamReader(frontStream))
				  .lines().collect(Collectors.joining("\n"));
			final String back = new BufferedReader(new InputStreamReader(backStream))
				  .lines().collect(Collectors.joining("\n"));
			return new PojoVocabularyView(aBatchId, css, front, back);
		} catch (final IOException e) {
			throw new CoreException("Failed to init view", e);
		}
	}

}
