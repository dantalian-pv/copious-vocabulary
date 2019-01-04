package ru.dantalian.copvoc.web.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.dantalian.copvoc.persist.api.config.AppConfigMap;

@ConfigurationProperties(prefix = "app")
@Configuration
public class AppConfig {

	private Map<String, Object> settings;

	@Bean
	public AppConfigMap getMap() {
		return new AppConfigMapImpl();
	}

	public Map<String, Object> getSettings() {
		return settings;
	}

	public void setSettings(final Map<String, Object> aSettings) {
		settings = aSettings;
	}

	class AppConfigMapImpl implements AppConfigMap {

		@Override
		public <T> T get(final String aProperty) {
			final String[] items = aProperty.split("\\.");
			Map<String, Object> map = settings;
			Object res = null;
			for (final String item: items) {
				res = map.get(item);
				if (res == null) {
					return null;
				}
				if (Map.class.isAssignableFrom(res.getClass())) {
					map = (Map<String, Object>) res;
				} else {
					return (T) res;
				}
			}
			return (T) res;
		}

	}

}
