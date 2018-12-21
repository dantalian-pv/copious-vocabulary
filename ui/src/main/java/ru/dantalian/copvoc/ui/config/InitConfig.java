package ru.dantalian.copvoc.ui.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistPrincipalManager;

@Configuration
public class InitConfig {

	@Autowired
	private PersistPrincipalManager principalManager;

	@Autowired
	private LanguageUtils languageUtils;

	@PostConstruct
	public void initData() throws CoreException, PersistException {
		initLanguages();
		initDefaultPrincipal();
	}

	public void initLanguages() throws CoreException, PersistException {
		languageUtils.upsertLanguages(languageUtils.getDefaultLanguages());
	}

	public void initDefaultPrincipal() throws PersistException {
		principalManager.createPrincipal("user", "user");
		final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		principalManager.storePasswordFor("user", "{bcrypt}" + passwordEncoder.encode("user"));
	}

}
