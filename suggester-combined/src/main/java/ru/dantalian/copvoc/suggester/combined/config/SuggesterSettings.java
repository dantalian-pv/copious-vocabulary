package ru.dantalian.copvoc.suggester.combined.config;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SuggesterSettings {

	@Value("${ru.dantalian.copvoc.suggesters}")
	private Set<String> enabledSuggesters;

	public Set<String> getEnabledSuggesters() {
		return enabledSuggesters;
	}

	@Bean
	@Scope("singleton")
	public ExecutorService getPool() {
		return Executors.newFixedThreadPool(16);
	}

}
