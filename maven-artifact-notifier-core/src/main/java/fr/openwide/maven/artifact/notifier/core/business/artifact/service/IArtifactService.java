package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.Date;
import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public interface IArtifactService extends IGenericEntityService<Long, Artifact> {
	
	Artifact getOrCreate(ArtifactKey artifactKey) throws ServiceException, SecurityServiceException;
	
	List<Long> listIds();
	
	List<Artifact> listByArtifactGroup(ArtifactGroup group);
	
	Artifact getByArtifactKey(ArtifactKey artifactKey);
	
	List<Artifact> listByStatus(ArtifactStatus status);

	List<ArtifactVersion> listArtifactVersionsAfterDate(Artifact artifact, Date date);
	
	List<Artifact> listRelatedDeprecatedArtifacts(Artifact artifact);
	
	List<Artifact> listMostFollowedArtifacts(int limit);
	
	List<Artifact> searchAutocomplete(String searchPattern, Integer limit, Integer offset) throws ServiceException;
	
	List<Artifact> searchAutocompleteWithoutProject(String searchPattern, Integer limit, Integer offset) throws ServiceException;

	List<Artifact> searchByName(String searchPattern, ArtifactDeprecationStatus deprecation, Integer limit, Integer offset);

	int countSearchByName(String searchTerm, ArtifactDeprecationStatus deprecation);

	List<Artifact> searchRecommended(String searchPattern, Integer limit, Integer offset) throws ServiceException;

	int countSearchRecommended(String searchTerm) throws ServiceException;

	boolean hasProject(Artifact artifact);

	List<Long> listIdsByStatus(ArtifactStatus status);
}
