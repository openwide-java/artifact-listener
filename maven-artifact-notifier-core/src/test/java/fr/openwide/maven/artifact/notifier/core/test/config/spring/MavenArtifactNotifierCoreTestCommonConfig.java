package fr.openwide.maven.artifact.notifier.core.test.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.EmptyNotificationContentDescriptorFactoryImpl;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.IArtifactNotifierNotificationContentDescriptorFactory;
import fr.openwide.maven.artifact.notifier.core.config.spring.MavenArtifactNotifierCoreCommonConfig;

@Configuration
@ConfigurationLocations(locations = { "classpath:configuration-test.properties" }, order = 1000)
@Import({
	MavenArtifactNotifierCoreCommonConfig.class,
})
public class MavenArtifactNotifierCoreTestCommonConfig {
	
	@Bean
	public IArtifactNotifierNotificationContentDescriptorFactory<?> notificationContentDescriptorFactory() {
		return new EmptyNotificationContentDescriptorFactoryImpl();
	}

}
