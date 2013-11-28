package fr.openwide.maven.artifact.notifier.web.application.notification.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
import fr.openwide.maven.artifact.notifier.web.application.navigation.form.PasswordPatternValidator;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ResetPasswordNotificationPage extends MainTemplate {

	private static final long serialVersionUID = 2819000772080260886L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordNotificationPage.class);

	@SpringBean
	private IUserService userService;
	
	private IModel<User> userModel;
	
	private IModel<String> passwordModel;
	
	private IModel<String> confirmPasswordModel;
	
	public ResetPasswordNotificationPage(PageParameters parameters) {
		super(parameters);
		
		User user = LinkUtils.extractUserFromHashPageParameter(userService, parameters, getApplication().getHomePage());
		userModel = new GenericEntityModel<Long, User>(user);
		passwordModel = Model.of();
		confirmPasswordModel = Model.of();

		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("home.pageTitle"), HomePage.linkDescriptor()));
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("notification.resetPassword.pageTitle"), getPageClass(), parameters));
		
		add(new Label("pageTitle", new ResourceModel("notification.resetPassword.pageTitle")));
		
		Form<User> form = new Form<User>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				try {
					User user = userModel.getObject();
					String passwordValue = passwordModel.getObject();
					String confirmPasswordValue = confirmPasswordModel.getObject();
					
					if (passwordValue != null && confirmPasswordValue != null) {
						userService.changePassword(user, passwordValue);
						
						getSession().success(getString("notification.resetPassword.success"));
						redirect(HomePage.class);
					}
				} catch (RestartResponseException e) {
					throw e;
				} catch (Exception e) {
					LOGGER.error("Error occured while validating account", e);
					getSession().error(getString("notification.resetPassword.error"));
				}
			}
		};
		add(form);
		
		// Password fields
		PasswordTextField passwordInput = new PasswordTextField("newPasswordInput", this.passwordModel);
		passwordInput.setLabel(new ResourceModel("register.password"));
		passwordInput.add(new PasswordPatternValidator());
		form.add(passwordInput);
		
		PasswordTextField confirmPasswordInput = new PasswordTextField("confirmPasswordInput", this.confirmPasswordModel);
		confirmPasswordInput.setLabel(new ResourceModel("register.confirmPassword"));
		form.add(confirmPasswordInput);
		
		form.add(new EqualPasswordInputValidator(passwordInput, confirmPasswordInput) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String resourceKey() {
				return "register.password.wrongConfirmation";
			}
		});
		form.add(new SubmitLink("submit"));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ResetPasswordNotificationPage.class;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (userModel != null) {
			this.userModel.detach();
		}
		if (passwordModel != null) {
			this.passwordModel.detach();
		}
		if (confirmPasswordModel != null) {
			confirmPasswordModel.detach();
		}
	}
}
