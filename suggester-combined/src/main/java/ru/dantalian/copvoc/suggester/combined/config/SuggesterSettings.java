package ru.dantalian.copvoc.suggester.combined.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SuggesterSettings {

	@Value("${ru.dantalian.copvoc.suggesters}")
	private Set<String> enabledSuggesters;

	public Set<String> getEnabledSuggesters() {
		return enabledSuggesters;
	}

}
