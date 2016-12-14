package fr.openwide.maven.artifact.notifier.core.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fr.openwide.core.jpa.externallinkchecker.config.spring.JpaExternalLinkCheckerConfig;
import fr.openwide.core.spring.config.spring.AbstractApplicationConfig;
import fr.openwide.core.spring.config.spring.annotation.ApplicationDescription;
import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;
import fr.openwide.maven.artifact.notifier.core.MavenArtifactNotifierCorePackage;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

@Configuration
@ApplicationDescription(name = MavenArtifactNotifierCoreCommonConfig.APPLICATION_NAME)
@ConfigurationLocations
@Import({
	Postgresql96WorkaroundConfig.class,						// override driver jdbc postgresql
	MavenArtifactNotifierCoreCommonJpaConfig.class,			// configuration de la persistence
	MavenArtifactNotifierCoreSecurityConfig.class,			// configuration de la sécurité
	MavenArtifactNotifierCoreNotificationConfig.class,		// configuration des notifications
	MavenArtifactNotifierCoreSolrConfig.class,				// configuration de la communication solr
	MavenArtifactNotifierCoreSchedulingConfig.class,		// configuration de l'ordonnancement de tâches
	MavenArtifactNotifierCoreTaskManagementConfig.class,
	MavenArtifactNotifierCorePropertyConfig.class,			// configuration des propriétés de l'application
	JpaExternalLinkCheckerConfig.class
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
	
	@Bean(name = { "configurer" })
	public MavenArtifactNotifierConfigurer configurer() {
		return new MavenArtifactNotifierConfigurer();
	}
}
