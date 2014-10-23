package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import java.util.List;
import java.util.Locale;

import fr.openwide.core.spring.notification.exception.NotificationContentRenderingException;
import fr.openwide.core.spring.notification.model.INotificationContentDescriptor;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;


/**
 * Mock implementation only used when running the tests.
 */
public class EmptyNotificationContentDescriptorFactoryImpl implements IArtifactNotifierNotificationContentDescriptorFactory<INotificationContentDescriptor> {
	
	private static final INotificationContentDescriptor NULL_DESCRIPTOR = new INotificationContentDescriptor() {
		@Override
		public String renderSubject(Locale locale) {
			return null;
		}
		
		@Override
		public String renderHtmlBody(Locale locale) throws NotificationContentRenderingException {
			return null;
		}
		
		@Override
		public String renderTextBody(Locale locale) throws NotificationContentRenderingException {
			return null;
		}
	};

	@Override
	public INotificationContentDescriptor renderDeleteEmailNotificationPanel(EmailAddress emailAddress) {
		return NULL_DESCRIPTOR;
	}

	@Override
	public INotificationContentDescriptor renderConfirmEmailNotificationPanel(EmailAddress emailAddress) {
		return NULL_DESCRIPTOR;
	}

	@Override
	public INotificationContentDescriptor renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications,
			EmailAddress emailAddress) {
		return NULL_DESCRIPTOR;
	}

	@Override
	public INotificationContentDescriptor renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, User user) {
		return NULL_DESCRIPTOR;
	}

	@Override
	public INotificationContentDescriptor renderResetPasswordNotificationPanel(User user) {
		return NULL_DESCRIPTOR;
	}

	@Override
	public INotificationContentDescriptor renderConfirmRegistrationNotificationPanel(User user) {
		return NULL_DESCRIPTOR;
	}

}
