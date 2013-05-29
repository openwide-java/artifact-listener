package fr.openwide.maven.artifact.notifier.web.application.notification.page;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailStatus;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IEmailAddressService;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ConfirmEmailNotificationPage extends MainTemplate {

	private static final long serialVersionUID = -6767518941118385548L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmEmailNotificationPage.class);

	@SpringBean
	private IEmailAddressService emailAddressService;
	
	private IModel<EmailAddress> emailAddressModel;
	
	public ConfirmEmailNotificationPage(PageParameters parameters) {
		super(parameters);
		
		EmailAddress emailAddress = LinkUtils.extractEmailFromHashPageParameter(emailAddressService, parameters, getApplication().getHomePage());
		emailAddressModel = new GenericEntityModel<Long, EmailAddress>(emailAddress);
		
		if (emailAddress.getStatus() != EmailStatus.PENDING_CONFIRM) {
			LOGGER.error("This email address is not pending for a confirmation");
			Session.get().error(getString("notification.email.confirm.wrongStatus"));

			redirect(getApplication().getHomePage());
			return;
		}

		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("home.pageTitle"), HomePage.class));
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("notification.email.confirm.pageTitle"), getPageClass(), parameters));
		
		add(new Label("pageTitle", new ResourceModel("notification.email.confirm.pageTitle")));
		
		add(new Link<EmailAddress>("confirmLink", emailAddressModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					emailAddressService.changeStatus(getModelObject(), EmailStatus.VALIDATED);
					Session.get().success(getString("notification.email.confirm.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while confirming email", e);
					Session.get().error(getString("notification.email.confirm.error"));
				}
				setResponsePage(getApplication().getHomePage());
			}
		});
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ConfirmEmailNotificationPage.class;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (emailAddressModel != null) {
			emailAddressModel.detach();
		}
	}
}
