package ru.dantalian.copvoc.core;

import java.io.Closeable;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.core.managers.LanguageManager;
import ru.dantalian.copvoc.core.managers.PrincipalManager;

@Service
public class CoreService implements Closeable {

	@Autowired
	private PrincipalManager principalManager;

	@Autowired
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
