package fr.openwide.maven.artifact.notifier.core.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import fr.openwide.maven.artifact.notifier.core.business.MavenArtifactNotifierCoreCommonBusinessPackage;
import fr.openwide.core.jpa.config.spring.provider.JpaPackageScanProvider;
import fr.openwide.core.jpa.security.config.spring.AbstractConfiguredJpaSecurityJpaConfig;

@Configuration
@EnableAspectJAutoProxy
public class MavenArtifactNotifierCoreCommonJpaConfig extends AbstractConfiguredJpaSecurityJpaConfig {

	/**
	 * Déclaration des packages de scan pour l'application.
	 */
	@Override
	@Bean
	public JpaPackageScanProvider applicationJpaPackageScanProvider() {
		return new JpaPackageScanProvider(MavenArtifactNotifierCoreCommonBusinessPackage.class.getPackage());
	}
}
