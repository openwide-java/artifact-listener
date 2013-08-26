package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IArtifactGroupDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;

@Service("artifactGroupService")
public class ArtifactGroupServiceImpl extends GenericEntityServiceImpl<Long, ArtifactGroup> implements IArtifactGroupService {

	@Autowired
	public ArtifactGroupServiceImpl(IArtifactGroupDao artifactGroupDao) {
		super(artifactGroupDao);
	}
	
	@Override
	public ArtifactGroup getOrCreate(String groupId) throws ServiceException, SecurityServiceException {
		ArtifactGroup artifactGroup = getByGroupId(groupId);
		if (artifactGroup == null) {
			artifactGroup = new ArtifactGroup(groupId);
			create(artifactGroup);
		}
		
		return artifactGroup;
	}
	
	@Override
	public ArtifactGroup getByGroupId(String groupId) {
		return getByNaturalId(groupId);
	}
}
