package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.spring.notification.service.AbstractNotificationServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Service("notificationService")
public class NotificationServiceImpl extends AbstractNotificationServiceImpl implements INotificationService {

	private static final String TPL_CONFIRM_REGISTRATION = "confirm_registration_notification.ftl";
	private static final String TPL_RESET_PASSWORD = "reset_password_notification.ftl";
	private static final String TPL_NEW_VERSION = "artifact_version_notification.ftl";
	private static final String TPL_CONFIRM_EMAIL = "confirm_email_notification.ftl";
	private static final String TPL_DELETE_EMAIL = "delete_email_notification.ftl";
	
	@Autowired
	private INotificationUrlBuilderService notificationUrlBuilderService;
	
	@Autowired
	private INotificationPanelRendererService notificationPanelRendererService;
	
	@Override
	public void sendConfirmRegistrationNotification(User user) throws ServiceException {
		String htmlPanel = notificationPanelRendererService.renderConfirmRegistrationNotificationPanel(user);
		String url = notificationUrlBuilderService.getConfirmRegistrationUrl(user);
		
		builder().to(user)
				.template(TPL_CONFIRM_REGISTRATION)
				.variable("user", user)
				.variable("url", url)
				.htmlBody(htmlPanel)
				.send();
	}
	
	@Override
	public void sendResetPasswordNotification(User user) throws ServiceException {
		String htmlPanel = notificationPanelRendererService.renderResetPasswordNotificationPanel(user);
		String url = notificationUrlBuilderService.getResetPasswordUrl(user);
		
		builder().to(user)
				.template(TPL_RESET_PASSWORD)
				.variable("url", url)
				.htmlBody(htmlPanel)
				.send();
	}
	
	@Override
	public void sendNewVersionNotification(List<ArtifactVersionNotification> notifications, User user) throws ServiceException {
		if (user.isNotificationAllowed()) {
			String htmlPanel = notificationPanelRendererService.renderNewVersionNotificationPanel(notifications, user);
			String unsubscribeUrl = notificationUrlBuilderService.getProfileUrl();

			builder().to(user)
					.template(TPL_NEW_VERSION)
					.variable("notifications", notifications)
					.variable("unsubscribeUrl", unsubscribeUrl)
					.htmlBody(htmlPanel)
					.send();
		}
		
		for (EmailAddress emailAddress : user.getAdditionalEmails()) {
			String htmlPanel = notificationPanelRendererService.renderNewVersionNotificationPanel(notifications, emailAddress);
			String unsubscribeUrl = notificationUrlBuilderService.getDeleteEmailUrl(emailAddress);

			builder().to(emailAddress)
					.template(TPL_NEW_VERSION)
					.variable("notifications", notifications)
					.variable("unsubscribeUrl", unsubscribeUrl)
					.htmlBody(htmlPanel)
					.send();
		}
	}
	
	@Override
	public void sendConfirmEmailNotification(EmailAddress emailAddress) throws ServiceException {
		String htmlPanel = notificationPanelRendererService.renderConfirmEmailNotificationPanel(emailAddress);
		String url = notificationUrlBuilderService.getConfirmEmailUrl(emailAddress);
		
		builder().to(emailAddress)
				.template(TPL_CONFIRM_EMAIL)
				.variable("email", emailAddress)
				.variable("url", url)
				.htmlBody(htmlPanel)
				.send();
	}
	
	@Override
	public void sendDeleteEmailNotification(EmailAddress emailAddress) throws ServiceException {
		String htmlPanel = notificationPanelRendererService.renderDeleteEmailNotificationPanel(emailAddress);
		String url = notificationUrlBuilderService.getDeleteEmailUrl(emailAddress);
		
		builder().to(emailAddress)
				.template(TPL_DELETE_EMAIL)
				.variable("email", emailAddress)
				.variable("url", url)
				.htmlBody(htmlPanel)
				.send();
	}
}
