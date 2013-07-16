package fr.openwide.maven.artifact.notifier.core.security.service;

import org.springframework.security.acls.model.Permission;

import fr.openwide.core.jpa.security.service.AbstractCorePermissionEvaluator;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public class MavenArtifactNotifierPermissionEvaluator extends AbstractCorePermissionEvaluator<User> {

	@Override
	protected boolean hasPermission(User user, Object targetDomainObject, Permission permission) {
		throw new IllegalArgumentException("ACL not used at the moment.");
	}

}
