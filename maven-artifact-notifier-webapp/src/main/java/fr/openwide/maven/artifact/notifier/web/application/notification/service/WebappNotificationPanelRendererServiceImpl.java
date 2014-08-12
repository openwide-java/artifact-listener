package fr.openwide.maven.artifact.notifier.web.application.notification.service;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Supplier;

import fr.openwide.core.wicket.more.notification.service.AbstractNotificationPanelRendererServiceImpl;
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

@Service("webappNotificationPanelRendererService")
public class WebappNotificationPanelRendererServiceImpl extends AbstractNotificationPanelRendererServiceImpl
		implements INotificationPanelRendererService {
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Override
	public String renderConfirmRegistrationNotificationPanel(final User user) {
		Supplier<Component> task = new Supplier<Component>() {
			@Override
			public Component get() {
				return new ConfirmRegistrationHtmlNotificationPanel("htmlPanel", Model.of(user));
			}
		};
		
		return renderComponent(task, user.getLocale());
	}
	
	@Override
	public String renderResetPasswordNotificationPanel(final User user) {
		Supplier<Component> task = new Supplier<Component>() {
			@Override
			public Component get() {
				return new ResetPasswordHtmlNotificationPanel("htmlPanel", Model.of(user));
			}
		};
		
		return renderComponent(task, user.getLocale());
	}
	
	@Override
	public String renderNewVersionNotificationPanel(final List<ArtifactVersionNotification> notifications, User user) {
		Supplier<Component> task = new Supplier<Component>() {
			@Override
			public Component get() {
				IModel<List<ArtifactVersionNotification>> notificationsModel = new ListModel<ArtifactVersionNotification>(notifications);
				return new NewVersionsHtmlNotificationPanel("htmlPanel", notificationsModel);
			}
		};
		
		return renderComponent(task, user.getLocale());
	}
	
	@Override
	public String renderNewVersionNotificationPanel(final List<ArtifactVersionNotification> notifications, final EmailAddress emailAddress) {
		Supplier<Component> task = new Supplier<Component>() {
			@Override
			public Component get() {
				IModel<List<ArtifactVersionNotification>> notificationsModel = new ListModel<ArtifactVersionNotification>(notifications);
				return new NewVersionsHtmlNotificationPanel("htmlPanel", notificationsModel, Model.of(emailAddress));
			}
		};
		
		return renderComponent(task, emailAddress.getLocale());
	}
	
	@Override
	public String renderConfirmEmailNotificationPanel(final EmailAddress emailAddress) {
		Supplier<Component> task = new Supplier<Component>() {
			@Override
			public Component get() {
				return new ConfirmEmailHtmlNotificationPanel("htmlPanel", Model.of(emailAddress));
			}
		};
		
		return renderComponent(task, emailAddress.getLocale());
	}
	
	@Override
	public String renderDeleteEmailNotificationPanel(final EmailAddress emailAddress) {
		Supplier<Component> task = new Supplier<Component>() {
			@Override
			public Component get() {
				return new DeleteEmailHtmlNotificationPanel("htmlPanel", Model.of(emailAddress));
			}
		};
		
		return renderComponent(task, emailAddress.getLocale());
	}
	
	@Override
	protected String getApplicationName() {
		return MavenArtifactNotifierApplication.NAME;
	}
}
