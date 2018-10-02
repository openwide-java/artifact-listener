package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import fr.openwide.core.jpa.business.generic.service.ITransactionalAspectAwareService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import org.apache.commons.lang3.mutable.MutableInt;

public interface IBatchArtifactService extends ITransactionalAspectAwareService {
	void synchronizeArtifact(Long artifactId, MutableInt versionsReleasedCount) throws ServiceException, SecurityServiceException;

	void notifyUsers() throws ServiceException, SecurityServiceException;
}
