package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationUrlBuilderService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public class DeleteEmailHtmlNotificationPanel extends AbstractHtmlNotificationPanel<EmailAddress> {

	private static final long serialVersionUID = 1676372998526497114L;
	
	@SpringBean
	private INotificationUrlBuilderService notificationUrlBuilderService;
	
	public DeleteEmailHtmlNotificationPanel(String id, IModel<EmailAddress> emailModel) {
		super(id, emailModel);
		
		add(new CustomWebMarkupContainer("titleContainer", STYLE_TITLE));
		
		WebMarkupContainer contentContainer = new CustomWebMarkupContainer("contentContainer", STYLE_CONTENT);
		add(contentContainer);
		
		ExternalLink confirmLink = new ExternalLink("confirmLink", getConfirmUrl());
		confirmLink.add(new StyleAttributeAppender(STYLE_LINK));
		contentContainer.add(confirmLink);
	}
	
	private String getConfirmUrl() {
		return notificationUrlBuilderService.getDeleteEmailUrl(getModelObject());
	}
}
