package ru.dantalian.copvoc.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="ru.dantalian.copvoc")
public class CopiousVocabularyWebMain {

	public static void main(final String[] args) {
		SpringApplication.run(CopiousVocabularyWebMain.class, args);
	}

}
