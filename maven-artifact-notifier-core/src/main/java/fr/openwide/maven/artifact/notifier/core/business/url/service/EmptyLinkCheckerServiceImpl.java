package fr.openwide.maven.artifact.notifier.core.business.url.service;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.url.model.ExternalLinkWrapper;

public class EmptyLinkCheckerServiceImpl implements ILinkCheckerService {

	@Override
	public void checkAllLinks() throws ServiceException, SecurityServiceException {
	}

	@Override
	public void checkLink(ExternalLinkWrapper link) throws ServiceException, SecurityServiceException {
	}
}
