package ru.dantalian.copvoc.ui.config;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.managers.LanguageManager;
import ru.dantalian.copvoc.core.managers.PrincipalManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.impl.model.personal.PojoLanguage;

@Configuration
public class InitConfig {

	@Autowired
	private LanguageManager languageManager;

	@Autowired
	private PrincipalManager principalManager;

	@PostConstruct
	public void initData() throws PersistException {
		initLanguages();
		initDefaultPrincipal();
	}

	public void initLanguages() throws PersistException {
		try {
			final ObjectMapper om = new ObjectMapper();
			try (InputStream langStream = this.getClass().getClassLoader()
					.getResourceAsStream(CoreConstants.DEFAULT_LANGUAGES)) {
				final ArrayNode arr = (ArrayNode) om.readTree(langStream).get("laguages");
				for (final JsonNode node: arr) {
					final PojoLanguage lang = om.treeToValue(node, PojoLanguage.class);
					final Language persistLang = languageManager.getLanguage(
							lang.getName(), lang.getCountry(), lang.getVariant());
					if (persistLang == null) {
						languageManager.createLanguage(lang.getName(), lang.getCountry(), lang.getVariant(), lang.getText());
					}
				}
			}
		} catch (final IOException e) {
			throw new PersistException("Failed to init languages", e);
		}
	}

	public void initDefaultPrincipal() throws PersistException {
		principalManager.createPrincipal("user", "user");
		final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		principalManager.storePasswordFor("user", "{bcrypt}" + passwordEncoder.encode("user"));
	}

}
