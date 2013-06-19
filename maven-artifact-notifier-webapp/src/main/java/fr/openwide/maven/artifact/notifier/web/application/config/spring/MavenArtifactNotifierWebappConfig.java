package fr.openwide.maven.artifact.notifier.web.application.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.core.wicket.more.config.spring.AbstractWebappConfig;
import fr.openwide.maven.artifact.notifier.core.config.spring.MavenArtifactNotifierCoreCommonConfig;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;

@Configuration
@Import({
	MavenArtifactNotifierCoreCommonConfig.class,
	MavenArtifactNotifierWebappSecurityConfig.class,
	MavenArtifactNotifierWebappCacheConfig.class
})
@ComponentScan(
		basePackageClasses = {
				MavenArtifactNotifierApplication.class,
		},
		excludeFilters = @Filter(Configuration.class)
)
public class MavenArtifactNotifierWebappConfig extends AbstractWebappConfig {

	@Override
	@Bean(name= { "MavenArtifactNotifierApplication", "application" })
	public MavenArtifactNotifierApplication application() {
		return new MavenArtifactNotifierApplication();
	}
}
