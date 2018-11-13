package ru.dantalian.copvoc.core;

import java.io.Closeable;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.dantalian.copvoc.core.managers.LanguageManager;
import ru.dantalian.copvoc.core.managers.PrincipalManager;

@Singleton
public class CoreService implements Closeable {

	@Inject
	private PrincipalManager principalManager;

	@Inject
	private LanguageManager languageManager;

	public PrincipalManager getPrincipalManager() {
		return principalManager;
	}

	public LanguageManager getLanguageManager() {
		return languageManager;
	}

	@Override
	public void close() throws IOException {
		principalManager.close();
		languageManager.close();
	}

}
