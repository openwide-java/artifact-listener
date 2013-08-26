package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;

public interface IArtifactGroupService extends IGenericEntityService<Long, ArtifactGroup> {
	
	ArtifactGroup getOrCreate(String groupId) throws ServiceException, SecurityServiceException;
	
	ArtifactGroup getByGroupId(String groupId);
}
