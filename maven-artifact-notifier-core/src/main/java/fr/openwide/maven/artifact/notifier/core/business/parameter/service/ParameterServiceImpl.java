package fr.openwide.maven.artifact.notifier.core.business.parameter.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.more.business.parameter.dao.IParameterDao;
import fr.openwide.core.jpa.more.business.parameter.service.AbstractParameterServiceImpl;

@Service("parameterService")
public class ParameterServiceImpl extends AbstractParameterServiceImpl implements IParameterService {

	private static final String LAST_SYNCHRONIZATION_DATE = "lastSynchronizationDate";
	
	@Autowired
	public ParameterServiceImpl(IParameterDao dao) {
		super(dao);
	}
	
	@Override
	public Date getLastSynchronizationDate() {
		return getDateValue(LAST_SYNCHRONIZATION_DATE);
	}
	
	@Override
	public void setLastSynchronizationDate(Date lastSynchronizationDate) throws ServiceException, SecurityServiceException {
		updateDateValue(LAST_SYNCHRONIZATION_DATE, lastSynchronizationDate);
	}
}
