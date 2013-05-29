package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import java.util.List;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

/**
 * Implémentation bouche-trou, uniquement pour combler la dépendance.
 */
public class EmptyNotificationPanelRendererServiceImpl implements INotificationPanelRendererService {
	
	@Override
	public String renderConfirmRegistrationNotificationPanel(User user) {
		return "";
	}
	
	@Override
	public String renderResetPasswordNotificationPanel(User user) {
		return "";
	}

	@Override
	public String renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, User user) {
		return "";
	}
	
	@Override
	public String renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications,
			EmailAddress emailAddress) {
		return "";
	}

	@Override
	public String renderConfirmEmailNotificationPanel(EmailAddress emailAddress) {
		return "";
	}

	@Override
	public String renderDeleteEmailNotificationPanel(EmailAddress emailAddress) {
		return "";
	}
}
