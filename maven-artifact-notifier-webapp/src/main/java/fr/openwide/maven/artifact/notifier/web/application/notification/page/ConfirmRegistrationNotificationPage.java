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
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ConfirmRegistrationNotificationPage extends MainTemplate {

	private static final long serialVersionUID = 2819000772080260886L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmRegistrationNotificationPage.class);

	@SpringBean
	private IUserService userService;
	
	private IModel<User> userModel;
	
	public ConfirmRegistrationNotificationPage(PageParameters parameters) {
		super(parameters);
		
		User user = LinkUtils.extractUserFromHashPageParameter(userService, parameters, getApplication().getHomePage());
		userModel = new GenericEntityModel<Long, User>(user);

		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("home.pageTitle"), HomePage.linkDescriptor()));
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("notification.register.confirm.pageTitle"), getPageClass(), parameters));
		
		add(new Label("pageTitle", new ResourceModel("notification.register.confirm.pageTitle")));
		
		add(new Link<User>("confirmLink", userModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					userService.confirmRegistration(userModel.getObject());
					Session.get().success(getString("notification.register.confirm.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while validating account", e);
					Session.get().error(getString("notification.register.confirm.error"));
				}
				setResponsePage(getApplication().getHomePage());
			}
		});
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ConfirmRegistrationNotificationPage.class;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (userModel != null) {
			userModel.detach();
		}
	}
}
