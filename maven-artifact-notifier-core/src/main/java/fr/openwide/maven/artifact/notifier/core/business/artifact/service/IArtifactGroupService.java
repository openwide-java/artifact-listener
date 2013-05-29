package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;

public interface IArtifactGroupService extends IGenericEntityService<Long, ArtifactGroup> {
	
	ArtifactGroup getByGroupId(String groupId);
}
