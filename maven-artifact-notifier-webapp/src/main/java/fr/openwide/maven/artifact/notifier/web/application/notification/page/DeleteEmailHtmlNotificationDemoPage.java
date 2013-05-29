package fr.openwide.maven.artifact.notifier.web.application.notification.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.console.page.ConsoleNotificationIndexPage;
import fr.openwide.maven.artifact.notifier.web.application.console.template.NotificationRendererTemplate;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.DeleteEmailHtmlNotificationPanel;

public class DeleteEmailHtmlNotificationDemoPage extends NotificationRendererTemplate {

	private static final long serialVersionUID = -6767518941118385548L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmRegistrationHtmlNotificationDemoPage.class);

	@SpringBean
	private IUserService userService;
	
	public DeleteEmailHtmlNotificationDemoPage(PageParameters parameters) {
		super(parameters);
		
		User user = userService.getByUserName(ConsoleNotificationIndexPage.DEFAULT_USERNAME);
		EmailAddress emailAddress = null;
		if (user != null) {
			emailAddress = Iterables.getFirst(user.getAdditionalEmails(), null);
		}
		if (user == null || emailAddress == null) {
			LOGGER.error("There is no user or email address available");
			Session.get().error(getString("console.notifications.noDataAvailable"));
			
			throw new RestartResponseException(ConsoleNotificationIndexPage.class);
		}

		if (emailAddress.getEmailHash() == null) {
			emailAddress.setEmailHash(userService.getHash(user, emailAddress.getEmail()));
		}
		
		add(new DeleteEmailHtmlNotificationPanel("htmlPanel", Model.of(emailAddress)));
	}
}
