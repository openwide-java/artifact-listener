package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.markup.html.form.LabelPlaceholderBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;

public class ForgottenPasswordPage extends MainTemplate {

	private static final long serialVersionUID = 289306551107177573L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ForgottenPasswordPage.class);
	
	@SpringBean
	private IUserService userService;
	
	private IModel<String> userNameModel;
	
	public ForgottenPasswordPage(PageParameters parameters) {
		super(parameters);
		
		if (AuthenticatedWebSession.exists() && AuthenticatedWebSession.get().isSignedIn()) {
			redirect(DashboardPage.class);
			return;
		}
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("forgottenPassword.pageTitle"), getPageClass()));
		
		add(new Label("pageTitle", new ResourceModel("forgottenPassword.pageTitle")));
		
		Form<Void> form = new Form<Void>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				try {
					User user = userService.getByUserName(userNameModel.getObject());
					if (user != null) {
						if (user.getOpenIdIdentifier() == null) {
							userService.passwordResetRequest(user);
							getSession().success(getString("forgottenPassword.success"));
							
							redirect(getApplication().getHomePage());
						} else {
							LOGGER.warn("The account '" + user.getUserName() + "' is not a classic account: its password can't be reset");
							getSession().warn(getString("forgottenPassword.account.openId"));
						}
					} else {
						LOGGER.error("The username '" + userNameModel.getObject() + "' does not match any existing account");
						getSession().error(getString("forgottenPassword.account.notFound"));
					}
				} catch (RestartResponseException e) {
					throw e;
				} catch (Exception e) {
					LOGGER.error("An error occurred while sending the forgotten password email", e);
					getSession().error(getString("forgottenPassword.error"));
				}
			}
		};
		add(form);
		
		userNameModel = Model.of("");
		EmailTextField userNameField = new EmailTextField("userName", userNameModel);
		userNameField.setRequired(true);
		userNameField.setLabel(new ResourceModel("home.identification.classic.email"));
		userNameField.add(new LabelPlaceholderBehavior());
		form.add(userNameField);
		
		form.add(new SubmitLink("submit"));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ForgottenPasswordPage.class;
	}
}
