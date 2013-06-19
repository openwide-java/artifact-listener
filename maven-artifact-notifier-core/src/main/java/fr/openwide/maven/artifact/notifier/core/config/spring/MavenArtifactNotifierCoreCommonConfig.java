package fr.openwide.maven.artifact.notifier.core.config.spring;

import java.net.MalformedURLException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fr.openwide.core.spring.config.spring.AbstractApplicationConfig;
import fr.openwide.core.spring.config.spring.annotation.ApplicationDescription;
import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;
import fr.openwide.maven.artifact.notifier.core.MavenArtifactNotifierCorePackage;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

@Configuration
@ApplicationDescription(name = MavenArtifactNotifierCoreCommonConfig.APPLICATION_NAME)
@ConfigurationLocations
@Import({
	MavenArtifactNotifierCoreCommonJpaConfig.class,			// configuration de la persistence
	MavenArtifactNotifierCoreSecurityConfig.class,			// configuration de la sécurité
	MavenArtifactNotifierCoreNotificationConfig.class,		// configuration des notifications
	MavenArtifactNotifierCoreSolrConfig.class,				// configuration de la communication solr
	MavenArtifactNotifierCoreSchedulingConfig.class			// configuration de l'ordonnancement de tâches
})
@ComponentScan(
	basePackageClasses = {
		MavenArtifactNotifierCorePackage.class
	},
	// https://jira.springsource.org/browse/SPR-8808
	// on veut charger de manière explicite le contexte ; de ce fait,
	// on ignore l'annotation @Configuration sur le scan de package.
	excludeFilters = @Filter(Configuration.class)
)
@EnableTransactionManagement
public class MavenArtifactNotifierCoreCommonConfig extends AbstractApplicationConfig {

	public static final String APPLICATION_NAME = "maven-artifact-notifier";

	public static final String PROFILE_TEST = "test";
	
	private static final String UTF8 = "UTF-8";

	/**
	 * L'obtention du configurer doit être statique.
	 */
	@Bean(name = { "configurer" })
	public static MavenArtifactNotifierConfigurer environment(ConfigurableApplicationContext context) throws MalformedURLException {
		MavenArtifactNotifierConfigurer configurer = new MavenArtifactNotifierConfigurer();
		configurer.setFileEncoding(UTF8);
		
		return configurer;
	}
}
