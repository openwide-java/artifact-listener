package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import java.util.List;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public interface INotificationPanelRendererService {

	String renderConfirmRegistrationNotificationPanel(User user);
	
	String renderResetPasswordNotificationPanel(User user);

	String renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, User user);
	
	String renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, EmailAddress emailAddress);
	
	String renderConfirmEmailNotificationPanel(EmailAddress emailAddress);
	
	String renderDeleteEmailNotificationPanel(EmailAddress emailAddress);
}
