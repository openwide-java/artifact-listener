package fr.openwide.maven.artifact.notifier.core.test.config.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.maven.artifact.notifier.core.config.spring.MavenArtifactNotifierCoreCommonConfig;
import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;

@Configuration
@ConfigurationLocations(locations = { "classpath:configuration-test.properties" }, order = 1000)
@Import({
	MavenArtifactNotifierCoreCommonConfig.class,
})
public class MavenArtifactNotifierCoreTestCommonConfig {

}
