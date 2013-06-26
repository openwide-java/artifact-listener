package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IArtifactVersionDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion_;

@Service("artifactVersionService")
public class ArtifactVersionServiceImpl extends GenericEntityServiceImpl<Long, ArtifactVersion> implements IArtifactVersionService {

	private IArtifactVersionDao artifactVersionDao;
	
	@Autowired
	public ArtifactVersionServiceImpl(IArtifactVersionDao artifactVersionDao) {
		super(artifactVersionDao);
		this.artifactVersionDao = artifactVersionDao;
	}
	
	@Override
	public List<ArtifactVersion> listByArtifact(Artifact artifact) {
		return listByField(ArtifactVersion_.artifact, artifact);
	}
	
	@Override
	public ArtifactVersion getByArtifactAndVersion(Artifact artifact, String version) {
		return artifactVersionDao.getByArtifactAndVersion(artifact, version);
	}
	
	@Override
	public List<ArtifactVersion> listRecentReleases(int limit) {
		return artifactVersionDao.listRecentReleases(limit);
	}
}
