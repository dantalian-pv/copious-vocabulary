package ru.dantalian.copvoc.ui.listener;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

public class AppReadyListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger logger = LoggerFactory.getLogger(AppReadyListener.class);

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent aEvent) {
		final ConfigurableEnvironment environment = aEvent.getApplicationContext().getEnvironment();
		final String[] activeProfiles = environment.getActiveProfiles();
		boolean prod = false;
		for (final String profile : activeProfiles) {
			if ("prod".equals(profile)) {
				prod = true;
				break;
			}
		}
		if (!prod) {
			return;
		}
		final String property = environment.getProperty("server.port");
		final URI uri = URI.create("https://localhost:" + property);
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (final IOException e) {
				logger.error("Failed to open a browser", e);
			}
		} else {
			final Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + uri);
			} catch (final IOException e) {
				logger.error("Failed to open a browser", e);
			}
		}
	}

}
