package fr.openwide.maven.artifact.notifier.core.security.acl.service;

import java.util.List;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.NotFoundException;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.security.acl.domain.CoreAcl;
import fr.openwide.core.jpa.security.acl.service.AbstractCoreAclServiceImpl;
import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;

public class MavenArtifactNotifierAclServiceImpl extends AbstractCoreAclServiceImpl {

	@Override
	protected boolean isCacheEnabled() {
		return false;
	}

	@Override
	protected List<AccessControlEntry> getAccessControlEntriesForEntity(
			CoreAcl acl, GenericEntity<?, ?> objectIdentityEntity)
			throws NotFoundException {
		List<AccessControlEntry> acls = Lists.newArrayListWithExpectedSize(2);
		acls.add(getAccessControlEntry(acl, CoreAuthorityConstants.ROLE_AUTHENTICATED, BasePermission.WRITE));
		acls.add(getAccessControlEntry(acl, CoreAuthorityConstants.ROLE_ADMIN, BasePermission.ADMINISTRATION));
		return acls;
	}
}
