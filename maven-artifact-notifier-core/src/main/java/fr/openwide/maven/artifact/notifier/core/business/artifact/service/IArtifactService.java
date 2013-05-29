package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.Date;
import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public interface IArtifactService extends IGenericEntityService<Long, Artifact> {
	
	List<Artifact> listByArtifactGroup(ArtifactGroup group);
	
	Artifact getByArtifactKey(ArtifactKey artifactKey);
	
	List<Artifact> listByStatus(ArtifactStatus status);

	List<ArtifactVersion> listArtifactVersionsAfterDate(Artifact artifact, Date date);
	
	List<Artifact> searchAutocomplete(String searchPattern) throws ServiceException, SecurityServiceException;

	List<Artifact> search(String searchPattern);

	List<Artifact> search(String searchPattern, Integer limit, Integer offset);

	int countSearch(String searchTerm);
}
