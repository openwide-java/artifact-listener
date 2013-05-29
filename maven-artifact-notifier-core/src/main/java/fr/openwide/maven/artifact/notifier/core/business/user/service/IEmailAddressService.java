package fr.openwide.maven.artifact.notifier.core.business.user.service;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailStatus;

public interface IEmailAddressService extends IGenericEntityService<Long, EmailAddress> {

	EmailAddress getByHash(String hash);

	void changeStatus(EmailAddress emailAddress, EmailStatus status) throws ServiceException, SecurityServiceException;
	
}
