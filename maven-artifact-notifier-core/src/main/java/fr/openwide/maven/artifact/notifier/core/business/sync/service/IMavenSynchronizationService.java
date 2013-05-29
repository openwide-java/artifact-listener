package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import java.util.List;

import fr.openwide.core.jpa.business.generic.service.ITransactionalAspectAwareService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;

public interface IMavenSynchronizationService extends ITransactionalAspectAwareService {

	void refreshAllLatestVersions() throws ServiceException, SecurityServiceException;
	
	void initializeAllArtifacts() throws ServiceException, SecurityServiceException, InterruptedException;

	void synchronizeArtifactsAndNotifyUsers(List<Artifact> artifacts) throws ServiceException, SecurityServiceException, InterruptedException;

	void synchronizeAllArtifactsAndNotifyUsers() throws ServiceException, SecurityServiceException, InterruptedException;
}
