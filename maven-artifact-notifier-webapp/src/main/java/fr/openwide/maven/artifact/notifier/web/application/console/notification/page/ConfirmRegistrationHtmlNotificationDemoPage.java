package fr.openwide.maven.artifact.notifier.web.application.console.notification.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.console.notification.template.NotificationRendererTemplate;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.ConfirmRegistrationHtmlNotificationPanel;

public class ConfirmRegistrationHtmlNotificationDemoPage extends NotificationRendererTemplate {

	private static final long serialVersionUID = 5307851545668673599L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmRegistrationHtmlNotificationDemoPage.class);

	@SpringBean
	private IUserService userService;
	
	public ConfirmRegistrationHtmlNotificationDemoPage(PageParameters parameters) {
		super(parameters);

		User user = userService.getByUserName(ConsoleNotificationIndexPage.DEFAULT_USERNAME);
		if (user == null) {
			LOGGER.error("There is no user available");
			Session.get().error(getString("console.notifications.noDataAvailable"));
			
			throw new RestartResponseException(ConsoleNotificationIndexPage.class);
		}
		user.setNotificationHash(userService.getHash(user, user.getUserName()));
		
		add(new ConfirmRegistrationHtmlNotificationPanel("htmlPanel", Model.of(user)));
		user.setNotificationHash(null);
	}
}
