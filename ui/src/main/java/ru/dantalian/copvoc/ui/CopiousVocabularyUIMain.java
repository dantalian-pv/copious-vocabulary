package ru.dantalian.copvoc.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="ru.dantalian.copvoc")
public class CopiousVocabularyUIMain {

	public static void main(final String[] args) {
		SpringApplication.run(CopiousVocabularyUIMain.class, args);
	}

}
