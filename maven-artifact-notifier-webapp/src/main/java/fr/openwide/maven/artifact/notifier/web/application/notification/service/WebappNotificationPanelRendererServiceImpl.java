package fr.openwide.maven.artifact.notifier.web.application.notification.service;

import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.lang.Args;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationPanelRendererService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.ConfirmEmailHtmlNotificationPanel;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.ConfirmRegistrationHtmlNotificationPanel;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.DeleteEmailHtmlNotificationPanel;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.NewVersionsHtmlNotificationPanel;
import fr.openwide.maven.artifact.notifier.web.application.notification.component.ResetPasswordHtmlNotificationPanel;
import fr.openwide.maven.artifact.notifier.web.application.notification.util.AbstractDummyThreadContextBuilder;

@Service("webappNotificationPanelRendererService")
public class WebappNotificationPanelRendererServiceImpl extends AbstractDummyThreadContextBuilder implements INotificationPanelRendererService {
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Override
	public String renderConfirmRegistrationNotificationPanel(User user) {
		Session session = getSession(MavenArtifactNotifierApplication.NAME);
		Component component = new ConfirmRegistrationHtmlNotificationPanel("htmlPanel", Model.of(user));
		
		return renderComponent(session, component, user.getLocale());
	}
	
	@Override
	public String renderResetPasswordNotificationPanel(User user) {
		Session session = getSession(MavenArtifactNotifierApplication.NAME);
		Component component = new ResetPasswordHtmlNotificationPanel("htmlPanel", Model.of(user));
		
		return renderComponent(session, component, user.getLocale());
	}
	
	@Override
	public String renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, User user) {
		Session session = getSession(MavenArtifactNotifierApplication.NAME);
		IModel<List<ArtifactVersionNotification>> notificationsModel = new ListModel<ArtifactVersionNotification>(notifications);
		Component component = new NewVersionsHtmlNotificationPanel("htmlPanel", notificationsModel);
		
		return renderComponent(session, component, user.getLocale());
	}
	
	@Override
	public String renderNewVersionNotificationPanel(List<ArtifactVersionNotification> notifications, EmailAddress emailAddress) {
		Session session = getSession(MavenArtifactNotifierApplication.NAME);
		IModel<List<ArtifactVersionNotification>> notificationsModel = new ListModel<ArtifactVersionNotification>(notifications);
		Component component = new NewVersionsHtmlNotificationPanel("htmlPanel", notificationsModel, Model.of(emailAddress));
		
		return renderComponent(session, component, emailAddress.getLocale());
	}
	
	@Override
	public String renderConfirmEmailNotificationPanel(EmailAddress emailAddress) {
		Session session = getSession(MavenArtifactNotifierApplication.NAME);
		Component component = new ConfirmEmailHtmlNotificationPanel("htmlPanel", Model.of(emailAddress));

		return renderComponent(session, component, emailAddress.getLocale());
	}
	
	@Override
	public String renderDeleteEmailNotificationPanel(EmailAddress emailAddress) {
		Session session = getSession(MavenArtifactNotifierApplication.NAME);
		Component component = new DeleteEmailHtmlNotificationPanel("htmlPanel", Model.of(emailAddress));

		return renderComponent(session, component, emailAddress.getLocale());
	}
	
	private String renderComponent(Session session, Component component, Locale locale) {
		Args.notNull(session, "session");
		Args.notNull(component, "component");
		
		Locale oldLocale = session.getLocale();
		if (locale != null) {
			session.setLocale(configurer.toAvailableLocale(locale));
		}
		String panel = ComponentRenderer.renderComponent(component).toString();
		
		session.setLocale(oldLocale);
		detachDummyThreadContext();
		return panel;
	}
}
