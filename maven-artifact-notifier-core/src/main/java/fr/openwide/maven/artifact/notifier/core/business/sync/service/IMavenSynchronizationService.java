package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;

import java.util.List;

public interface IMavenSynchronizationService {

	void refreshAllLatestVersions() throws ServiceException, SecurityServiceException;
	
	void initializeAllArtifacts() throws ServiceException, SecurityServiceException, InterruptedException;

	void synchronizeAllArtifactsAndNotifyUsers() throws ServiceException, SecurityServiceException, InterruptedException;

	void synchronizeArtifactsAndNotifyUsers(List<Long> artifactIds) throws ServiceException, SecurityServiceException,
			InterruptedException;
}
