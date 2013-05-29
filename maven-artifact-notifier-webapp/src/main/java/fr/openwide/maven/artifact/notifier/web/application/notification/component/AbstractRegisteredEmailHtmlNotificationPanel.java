package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public abstract class AbstractRegisteredEmailHtmlNotificationPanel<T> extends AbstractHtmlNotificationPanel<T> {
	
	private static final long serialVersionUID = 537218611209529287L;
	
	private IModel<EmailAddress> emailAddressModel;
	
	public AbstractRegisteredEmailHtmlNotificationPanel(String id, IModel<T> model,
			IModel<EmailAddress> emailAddressModel) {
		super(id, model);
		this.emailAddressModel = emailAddressModel;
		
		WebMarkupContainer unsubscribe = new WebMarkupContainer("unsubscribe");
		unsubscribe.add(new StyleAttributeAppender(STYLE_UNSUBSCRIBE));
		add(unsubscribe);
		
		unsubscribe.add(new Label("unsubscribeText", getUnsubscribeText()));
		
		ExternalLink unsubscribeLink = new ExternalLink("unsubscribeLink", getUnsubscribeUrl());
		unsubscribeLink.add(new StyleAttributeAppender(STYLE_LINK_FOOTER));
		unsubscribe.add(unsubscribeLink);
	}
	
	private IModel<String> getUnsubscribeText() {
		if (Mode.LINKED_EMAIL_ADDRESS.equals(getRegisteredMode())) {
			return new ResourceModel("notification.panel.unsubscribe.deleteEmail");
		}
		return new ResourceModel("notification.panel.unsubscribe.changeSettings");
	}
	
	private String getUnsubscribeUrl() {
		if (Mode.LINKED_EMAIL_ADDRESS.equals(getRegisteredMode())) {
			return notificationUrlBuilderService.getDeleteEmailUrl(emailAddressModel.getObject());
		}
		return notificationUrlBuilderService.getProfileUrl();
	}
	
	private Mode getRegisteredMode() {
		if (emailAddressModel != null && emailAddressModel.getObject() != null) {
			return Mode.LINKED_EMAIL_ADDRESS;
		}
		return Mode.REGISTERED_USER;
	}
	
	private enum Mode {
		REGISTERED_USER,
		LINKED_EMAIL_ADDRESS
	}
}
