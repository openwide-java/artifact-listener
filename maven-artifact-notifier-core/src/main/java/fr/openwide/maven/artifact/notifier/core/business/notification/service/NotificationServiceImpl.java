package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.fluid.AbstractNotificationServiceImpl;
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
		
		try {
			builder().to(user)
					.variable("user", user)
					.variable("url", url)
					.template(TPL_CONFIRM_REGISTRATION)
					.htmlBody(htmlPanel)
					.send();
		} catch (Exception e) {
			throw new ServiceException("Error during send mail process", e);
		}
	}
	
	@Override
	public void sendResetPasswordNotification(User user) throws ServiceException {
		String htmlPanel = notificationPanelRendererService.renderResetPasswordNotificationPanel(user);
		String url = notificationUrlBuilderService.getResetPasswordUrl(user);
		
		try {
			builder().to(user)
					.variable("url", url)
					.template(TPL_RESET_PASSWORD)
					.htmlBody(htmlPanel)
					.send();
		} catch (Exception e) {
			throw new ServiceException("Error during send mail process", e);
		}
	}
	
	@Override
	public void sendNewVersionNotification(List<ArtifactVersionNotification> notifications, User user) throws ServiceException {
		if (user.isNotificationAllowed()) {
			String htmlPanel = notificationPanelRendererService.renderNewVersionNotificationPanel(notifications, user);
			String unsubscribeUrl = notificationUrlBuilderService.getProfileUrl();

			try {
				builder().to(user)
						.variable("notifications", notifications)
						.variable("unsubscribeUrl", unsubscribeUrl)
						.template(TPL_NEW_VERSION)
						.htmlBody(htmlPanel)
						.send();
			} catch (Exception e) {
				throw new ServiceException("Error during send mail process", e);
			}
		}
		
		for (EmailAddress emailAddress : user.getAdditionalEmails()) {
			String htmlPanel = notificationPanelRendererService.renderNewVersionNotificationPanel(notifications, emailAddress);
			String unsubscribeUrl = notificationUrlBuilderService.getDeleteEmailUrl(emailAddress);

			try {
				builder().to(emailAddress)
						.variable("notifications", notifications)
						.variable("unsubscribeUrl", unsubscribeUrl)
						.template(TPL_NEW_VERSION)
						.htmlBody(htmlPanel)
						.send();
			} catch (Exception e) {
				throw new ServiceException("Error during send mail process", e);
			}
		}
	}
	
	@Override
	public void sendConfirmEmailNotification(EmailAddress emailAddress) throws ServiceException {
		String htmlPanel = notificationPanelRendererService.renderConfirmEmailNotificationPanel(emailAddress);
		String url = notificationUrlBuilderService.getConfirmEmailUrl(emailAddress);
		
		try {
			builder().to(emailAddress)
					.variable("email", emailAddress)
					.variable("url", url)
					.template(TPL_CONFIRM_EMAIL)
					.htmlBody(htmlPanel)
					.send();
		} catch (Exception e) {
			throw new ServiceException("Error during send mail process", e);
		}
	}
	
	@Override
	public void sendDeleteEmailNotification(EmailAddress emailAddress) throws ServiceException {
		String htmlPanel = notificationPanelRendererService.renderDeleteEmailNotificationPanel(emailAddress);
		String url = notificationUrlBuilderService.getDeleteEmailUrl(emailAddress);
		
		try {
			builder().to(emailAddress)
					.variable("email", emailAddress)
					.variable("url", url)
					.template(TPL_DELETE_EMAIL)
					.htmlBody(htmlPanel)
					.send();
		} catch (Exception e) {
			throw new ServiceException("Error during send mail process", e);
		}
	}
}
