package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public class ConfirmEmailHtmlNotificationPanel extends AbstractHtmlNotificationPanel<EmailAddress> {

	private static final long serialVersionUID = 1676372998526497114L;
	
	public ConfirmEmailHtmlNotificationPanel(String id, IModel<EmailAddress> emailModel) {
		super(id, emailModel);
		
		add(new CustomWebMarkupContainer("titleContainer", STYLE_TITLE));
		
		WebMarkupContainer contentContainer = new CustomWebMarkupContainer("contentContainer", STYLE_CONTENT);
		add(contentContainer);
		
		contentContainer.add(new Label("intro", new StringResourceModel("notification.panel.confirmEmail.text", getModel())));
		
		ExternalLink confirmLink = new ExternalLink("confirmLink", getConfirmUrl());
		confirmLink.add(new StyleAttributeAppender(STYLE_LINK));
		contentContainer.add(confirmLink);
	}
	
	private String getConfirmUrl() {
		return notificationUrlBuilderService.getConfirmEmailUrl(getModelObject());
	}
}
