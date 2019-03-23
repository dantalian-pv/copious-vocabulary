package ru.dantalian.copvoc.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.CoreException;

public final class ViewUtils {

	private static volatile String head;

	private static volatile String bottom;

	private ViewUtils() {
	}

	public static String getHead() throws CoreException {
		if (head == null) {
			try (final InputStream headStream = ViewUtils.class.getClassLoader()
					.getResourceAsStream(CoreConstants.DEFAULT_CARD_VIEW_HEAD)) {
				head = new BufferedReader(new InputStreamReader(headStream))
				  .lines().collect(Collectors.joining("\n"));
			} catch (final IOException e) {
				throw new CoreException("Failed to read head", e);
			}
		}
		return head;
	}

	public static String getBottom() throws CoreException {
		if (bottom == null) {
			try (final InputStream bottomStream = ViewUtils.class.getClassLoader()
					.getResourceAsStream(CoreConstants.DEFAULT_CARD_VIEW_BOTTOM)) {
				bottom = new BufferedReader(new InputStreamReader(bottomStream))
				  .lines().collect(Collectors.joining("\n"));
			} catch (final IOException e) {
				throw new CoreException("Failed to read bottom", e);
			}
		}
		return bottom;
	}

}
