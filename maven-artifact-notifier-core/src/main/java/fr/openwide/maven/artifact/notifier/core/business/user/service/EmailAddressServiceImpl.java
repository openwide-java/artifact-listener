package fr.openwide.maven.artifact.notifier.core.business.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.user.dao.IEmailAddressDao;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress_;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailStatus;

@Service("emailAddressService")
public class EmailAddressServiceImpl extends GenericEntityServiceImpl<Long, EmailAddress> implements IEmailAddressService {

	@Autowired
	public EmailAddressServiceImpl(IEmailAddressDao emailAddressDao) {
		super(emailAddressDao);
	}
	
	@Override
	public EmailAddress getByHash(String hash) {
		return getByField(EmailAddress_.emailHash, hash);
	}
	
	@Override
	public void changeStatus(EmailAddress emailAddress, EmailStatus status) throws ServiceException, SecurityServiceException {
		emailAddress.setStatus(status);
		update(emailAddress);
	}
}
