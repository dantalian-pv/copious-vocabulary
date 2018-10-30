package ru.dantalian.copvoc.cli;

import java.io.File;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "copious-vocabulary-cli")
public class CliOptions {

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "Display help")
	private boolean help;

	@Option(names = { "-c", "--config" }, paramLabel = "Config",
			description = "Path to configuration directory", defaultValue=".copious-vocabulary/config")
	private File config;

	@Option(names = { "-d", "--data" }, paramLabel = "Data",
			description = "Path to data directory", defaultValue=".copious-vocabulary/data")
	private File data;

	public boolean isHelp() {
		return help;
	}

	public File getConfig() {
		return config;
	}

	public File getData() {
		return data;
	}

}
