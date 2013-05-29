package fr.openwide.maven.artifact.notifier.init.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.maven.artifact.notifier.core.config.spring.MavenArtifactNotifierCoreCommonConfig;
import fr.openwide.maven.artifact.notifier.init.MavenArtifactNotifierInitPackage;
import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;

@Configuration
@Import({
	MavenArtifactNotifierCoreCommonConfig.class
})
@ConfigurationLocations(locations = "classpath:configuration-init.properties")
@ComponentScan(
		basePackageClasses = {
			MavenArtifactNotifierInitPackage.class
		}
)
public class MavenArtifactNotifierInitConfig {
}
