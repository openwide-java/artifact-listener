package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IArtifactDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact_;

@Service("artifactService")
public class ArtifactServiceImpl extends GenericEntityServiceImpl<Long, Artifact> implements IArtifactService {
	
	private IArtifactDao artifactDao;
	
	@Autowired
	public ArtifactServiceImpl(IArtifactDao artifactDao) {
		super(artifactDao);
		this.artifactDao = artifactDao;
	}

	@Override
	public List<Artifact> listByArtifactGroup(ArtifactGroup group) {
		return listByField(Artifact_.group, group);
	}
	
	@Override
	public List<Artifact> listByStatus(ArtifactStatus status) {
		return listByField(Artifact_.status, status);
	}

	@Override
	public Artifact getByArtifactKey(ArtifactKey artifactKey) {
		return artifactDao.getByGroupIdArtifactId(artifactKey.getGroupId(), artifactKey.getArtifactId());
	}
	
	@Override
	public List<ArtifactVersion> listArtifactVersionsAfterDate(Artifact artifact, Date date) {
		return artifactDao.listArtifactVersionsAfterDate(artifact, date);
	}
	
	@Override
	public List<Artifact> listRelatedDeprecatedArtifacts(Artifact artifact) {
		return artifactDao.listByField(Artifact_.relatedArtifact, artifact);
	}
	
	@Override
	public List<Artifact> searchAutocomplete(String searchPattern, Integer limit, Integer offset) throws ServiceException {
		return artifactDao.searchAutocomplete(searchPattern, limit, offset);
	}
	
	@Override
	public List<Artifact> searchByName(String searchPattern, ArtifactDeprecationStatus deprecation, Integer limit, Integer offset) {
		return artifactDao.searchByName(searchPattern, deprecation, limit, offset);
	}
	
	@Override
	public int countSearchByName(String searchTerm, ArtifactDeprecationStatus deprecation) {
		return artifactDao.countSearchByName(searchTerm, deprecation);
	}
	
	@Override
	public List<Artifact> searchRecommended(String searchPattern, Integer limit, Integer offset) {
		return artifactDao.searchRecommended(searchPattern, limit, offset);
	}
	
	@Override
	public int countSearchRecommended(String searchTerm) {
		return artifactDao.countSearchRecommended(searchTerm);
	}
}
