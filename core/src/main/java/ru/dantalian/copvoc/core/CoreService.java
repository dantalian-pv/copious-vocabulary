package ru.dantalian.copvoc.core;

import java.io.Closeable;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.dantalian.copvoc.core.managers.PrincipalManager;

@Singleton
public class CoreService implements Closeable {

	@Inject
	private PrincipalManager principalManager;

	public PrincipalManager getPrincipalManager() {
		return principalManager;
	}

	@Override
	public void close() throws IOException {
		principalManager.close();
	}

}
