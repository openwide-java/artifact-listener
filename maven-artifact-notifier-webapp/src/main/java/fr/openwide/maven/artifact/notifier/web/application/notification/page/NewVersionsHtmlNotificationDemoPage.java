package fr.openwide.maven.artifact.notifier.web.application.notification.page;

import java.util.List;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.console.page.ConsoleNotificationIndexPage;
import fr.openwide.maven.artifact.notifier.web.application.console.template.NotificationRendererTemplate;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.NewVersionsHtmlNotificationPanel;

public class NewVersionsHtmlNotificationDemoPage extends NotificationRendererTemplate {

	private static final long serialVersionUID = 5307851545668673599L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmRegistrationHtmlNotificationDemoPage.class);

	@SpringBean
	private IUserService userService;

	public NewVersionsHtmlNotificationDemoPage(PageParameters parameters) {
		super(parameters);
		
		User user = userService.getByUserName(ConsoleNotificationIndexPage.DEFAULT_USERNAME);
		if (user == null) {
			LOGGER.error("There is no user or email address available");
			Session.get().error(getString("console.notifications.noDataAvailable"));
			
			throw new RestartResponseException(ConsoleNotificationIndexPage.class);
		}
		List<ArtifactVersionNotification> notifications = userService.listRecentNotifications(user);
		
		IModel<List<ArtifactVersionNotification>> notificationsModel = new ListModel<ArtifactVersionNotification>(notifications);
		add(new NewVersionsHtmlNotificationPanel("htmlPanel", notificationsModel));
	}
}
