package fr.openwide.maven.artifact.notifier.core.business.parameter.service;

import java.util.Date;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.more.business.parameter.service.IAbstractParameterService;

public interface IParameterService extends IAbstractParameterService {

	Date getLastSynchronizationDate();

	void setLastSynchronizationDate(Date lastSynchronizationDate) throws ServiceException, SecurityServiceException;
}
