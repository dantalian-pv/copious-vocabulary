package ru.dantalian.copvoc.ui.config;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.Configs;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistPrincipalManager;
import ru.dantalian.copvoc.persist.elastic.config.ElasticSettings;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class InitConfig {

	private static final Logger logger = LoggerFactory.getLogger(InitConfig.class);

	@Autowired
	private PersistPrincipalManager principalManager;

	@Autowired
	private LanguageUtils languageUtils;

	@Value("${data.dir}")
	private File dataDir;

	@Autowired
	private ElasticSettings settings;

	private ElasticsearchClusterRunner runner;

	@PostConstruct
	public void initData() throws CoreException, PersistException {
		initElastic();
		initLanguages();
		initDefaultPrincipal();
	}

	@PreDestroy
	public void destroy() throws IOException {
		runner.close();
	}

	private void initElastic() {
		final Path baseDir = dataDir.toPath().resolve("elastic");
		baseDir.toFile().mkdirs();
		logger.info("Running Elasticsearch with data dir {}", baseDir);
		runner = new ElasticsearchClusterRunner();
		final Configs cfg = new Configs();
		cfg.basePath(baseDir.toString());
		cfg.baseHttpPort(settings.getElasticHosts().get(0).getPort());
		cfg.numOfNode(1);
		// create ES nodes
		runner.onBuild(new ElasticsearchClusterRunner.Builder() {
			@Override
			public void build(final int number, final Settings.Builder settingsBuilder) {
				// settingsBuilder.put("index.number_of_replicas", 0);
				settingsBuilder.put("http.port", settings.getElasticHosts().get(0).getPort());
				settingsBuilder.put("transport.tcp.port", settings.getElasticHosts().get(0).getPort() + 100);
			}
		}).build(cfg);
		runner.waitForRelocation();
		runner.ensureYellow();
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
