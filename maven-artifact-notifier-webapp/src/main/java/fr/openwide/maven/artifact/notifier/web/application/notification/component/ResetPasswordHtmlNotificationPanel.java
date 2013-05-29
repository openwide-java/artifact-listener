package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public class ResetPasswordHtmlNotificationPanel extends AbstractHtmlNotificationPanel<User> {

	private static final long serialVersionUID = 8075925515569405813L;

	public ResetPasswordHtmlNotificationPanel(String id, IModel<User> userModel) {
		super(id, userModel);
		
		WebMarkupContainer title = new WebMarkupContainer("title");
		title.add(new StyleAttributeAppender(STYLE_TITLE));
		add(title);
		
		WebMarkupContainer contentContainer = new CustomWebMarkupContainer("contentContainer", STYLE_CONTENT);
		add(contentContainer);
		
		contentContainer.add(new Label("text", new StringResourceModel("notification.panel.resetPassword.text", getModel())));
		
		contentContainer.add(new Label("confirmText", new ResourceModel("notification.panel.resetPassword.confirm")));
		
		ExternalLink confirmLink = new ExternalLink("confirmLink", getResetPasswordUrl());
		confirmLink.add(new StyleAttributeAppender(STYLE_LINK));
		confirmLink.add(new Label("confirmLabel", new ResourceModel("notification.panel.resetPassword.confirm.label")));
		contentContainer.add(confirmLink);
	}
	
	private String getResetPasswordUrl() {
		return notificationUrlBuilderService.getResetPasswordUrl(getModelObject());
	}
}
