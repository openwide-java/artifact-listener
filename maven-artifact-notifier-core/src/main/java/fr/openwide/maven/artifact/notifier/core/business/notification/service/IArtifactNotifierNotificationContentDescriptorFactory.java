package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import java.util.List;

import fr.openwide.core.spring.notification.model.INotificationContentDescriptor;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public interface IArtifactNotifierNotificationContentDescriptorFactory<TDescriptor extends INotificationContentDescriptor> {

	TDescriptor renderDeleteEmailNotificationPanel(EmailAddress emailAddress);

	TDescriptor renderConfirmEmailNotificationPanel(EmailAddress emailAddress);

	TDescriptor renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, EmailAddress emailAddress);

	TDescriptor renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, User user);

	TDescriptor renderResetPasswordNotificationPanel(User user);

	TDescriptor renderConfirmRegistrationNotificationPanel(User user);

}
