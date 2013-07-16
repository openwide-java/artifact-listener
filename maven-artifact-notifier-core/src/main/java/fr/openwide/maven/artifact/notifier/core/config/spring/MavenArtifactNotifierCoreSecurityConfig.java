package fr.openwide.maven.artifact.notifier.core.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import fr.openwide.core.jpa.security.config.spring.AbstractJpaSecurityConfig;
import fr.openwide.core.jpa.security.service.AuthenticationUserNameComparison;
import fr.openwide.maven.artifact.notifier.core.security.service.MavenArtifactNotifierPermissionEvaluator;

@Configuration
public class MavenArtifactNotifierCoreSecurityConfig extends AbstractJpaSecurityConfig {

	@Bean
	@Override
	public AuthenticationUserNameComparison authenticationUserNameComparison() {
		return AuthenticationUserNameComparison.CASE_INSENSITIVE;
	}
	
	@Bean
	@Override
	@Scope(proxyMode = ScopedProxyMode.INTERFACES)
	public MavenArtifactNotifierPermissionEvaluator permissionEvaluator() {
		return new MavenArtifactNotifierPermissionEvaluator();
	}

	@Override
	public String roleHierarchyAsString() {
		return defaultRoleHierarchyAsString();
	}

	@Override
	public String permissionHierarchyAsString() {
		return defaultPermissionHierarchyAsString();
	}

}
