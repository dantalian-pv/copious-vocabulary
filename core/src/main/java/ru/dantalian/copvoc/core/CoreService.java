package ru.dantalian.copvoc.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.core.managers.LanguageManager;
import ru.dantalian.copvoc.core.managers.PrincipalManager;

@Service
public class CoreService {

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

}
