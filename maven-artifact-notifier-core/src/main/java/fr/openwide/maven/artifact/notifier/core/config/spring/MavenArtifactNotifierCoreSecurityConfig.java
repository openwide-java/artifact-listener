package fr.openwide.maven.artifact.notifier.core.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.Permission;

import fr.openwide.maven.artifact.notifier.core.security.acl.service.MavenArtifactNotifierAclServiceImpl;
import fr.openwide.core.jpa.security.config.spring.AbstractJpaSecurityConfig;
import fr.openwide.core.jpa.security.service.AuthenticationUserNameComparison;

@Configuration
public class MavenArtifactNotifierCoreSecurityConfig extends AbstractJpaSecurityConfig {

	@Bean
	@Override
	public AuthenticationUserNameComparison authenticationUserNameComparison() {
		return AuthenticationUserNameComparison.CASE_INSENSITIVE;
	}

	@Bean
	@Override
	public AclService aclService() {
		return new MavenArtifactNotifierAclServiceImpl();
	}

	@Override
	public Class<? extends Permission> permissionClass() {
		return BasePermission.class;
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
