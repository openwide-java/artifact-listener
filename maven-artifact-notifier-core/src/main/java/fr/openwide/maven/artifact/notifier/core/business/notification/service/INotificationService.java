package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import java.util.List;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public interface INotificationService {

	void sendConfirmRegistrationNotification(User user) throws ServiceException;

	void sendResetPasswordNotification(User user) throws ServiceException;

	void sendNewVersionNotification(List<ArtifactVersionNotification> notification, User user) throws ServiceException;
	
	void sendConfirmEmailNotification(EmailAddress emailAddress) throws ServiceException;
	
	void sendDeleteEmailNotification(EmailAddress emailAddress) throws ServiceException;
}
