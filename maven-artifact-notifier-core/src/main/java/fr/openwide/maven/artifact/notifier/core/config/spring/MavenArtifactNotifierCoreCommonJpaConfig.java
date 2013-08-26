package fr.openwide.maven.artifact.notifier.core.config.spring;

import javax.persistence.spi.PersistenceProvider;

import org.hibernate.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import fr.openwide.core.jpa.config.spring.provider.JpaPackageScanProvider;
import fr.openwide.core.jpa.hibernate.ejb.InterceptorAwareHibernatePersistence;
import fr.openwide.core.jpa.security.config.spring.AbstractConfiguredJpaSecurityJpaConfig;
import fr.openwide.maven.artifact.notifier.core.business.MavenArtifactNotifierCoreCommonBusinessPackage;
import fr.openwide.maven.artifact.notifier.core.business.url.hibernate.ExternalLinkWrapperInterceptor;

@Configuration
@EnableAspectJAutoProxy
public class MavenArtifactNotifierCoreCommonJpaConfig extends AbstractConfiguredJpaSecurityJpaConfig {

	@Bean
	public PersistenceProvider persistenceProvider() {
		return new InterceptorAwareHibernatePersistence();
	}
	
	@Bean
	public Interceptor hibernateInterceptor() {
		return new ExternalLinkWrapperInterceptor();
	}
	
	/**
	 * Déclaration des packages de scan pour l'application.
	 */
	@Override
	@Bean
	public JpaPackageScanProvider applicationJpaPackageScanProvider() {
		return new JpaPackageScanProvider(MavenArtifactNotifierCoreCommonBusinessPackage.class.getPackage());
	}
}
