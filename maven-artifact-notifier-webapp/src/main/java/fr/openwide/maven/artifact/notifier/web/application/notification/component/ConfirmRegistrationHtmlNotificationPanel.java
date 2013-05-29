package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public class ConfirmRegistrationHtmlNotificationPanel extends AbstractHtmlNotificationPanel<User> {

	private static final long serialVersionUID = 1676372998526497114L;
	
	public ConfirmRegistrationHtmlNotificationPanel(String id, IModel<User> userModel) {
		super(id, userModel);
		
		WebMarkupContainer title = new WebMarkupContainer("title");
		title.add(new StyleAttributeAppender(STYLE_TITLE));
		add(title);
		
		WebMarkupContainer contentContainer = new CustomWebMarkupContainer("contentContainer", STYLE_CONTENT);
		add(contentContainer);
		
		contentContainer.add(new Label("text", new StringResourceModel("notification.panel.confirmRegistration.text", getModel())));
		
		contentContainer.add(new Label("confirmText", new ResourceModel("notification.panel.confirmRegistration.confirm")));
		
		ExternalLink confirmLink = new ExternalLink("confirmLink", getConfirmUrl());
		confirmLink.add(new StyleAttributeAppender(STYLE_LINK));
		confirmLink.add(new Label("confirmLabel", new ResourceModel("notification.panel.confirmRegistration.confirm.label")));
		contentContainer.add(confirmLink);
	}
	
	private String getConfirmUrl() {
		return notificationUrlBuilderService.getConfirmRegistrationUrl(getModelObject());
	}
}
