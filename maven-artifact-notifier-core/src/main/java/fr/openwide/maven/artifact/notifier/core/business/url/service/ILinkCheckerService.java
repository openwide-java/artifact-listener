package fr.openwide.maven.artifact.notifier.core.business.url.service;

import java.net.URISyntaxException;

import fr.openwide.core.jpa.business.generic.service.ITransactionalAspectAwareService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;

public interface ILinkCheckerService extends ITransactionalAspectAwareService {
	
	void checkAllLinks() throws ServiceException, SecurityServiceException, URISyntaxException;
	
	void checkLink(ExternalLinkWrapper link) throws ServiceException, SecurityServiceException;
}
