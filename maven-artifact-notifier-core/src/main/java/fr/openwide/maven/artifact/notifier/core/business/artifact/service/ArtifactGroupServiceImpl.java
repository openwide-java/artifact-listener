package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IArtifactGroupDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;

@Service("artifactGroupService")
public class ArtifactGroupServiceImpl extends GenericEntityServiceImpl<Long, ArtifactGroup> implements IArtifactGroupService {

	@Autowired
	public ArtifactGroupServiceImpl(IArtifactGroupDao artifactGroupDao) {
		super(artifactGroupDao);
	}
	
	@Override
	public ArtifactGroup getByGroupId(String groupId) {
		return getByNaturalId(groupId);
	}
}
