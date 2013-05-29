package fr.openwide.maven.artifact.notifier.web.application.navigation.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class RegisterFormPanel extends Panel {

	private static final long serialVersionUID = 6273289257800090393L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterFormPanel.class);
	
	@SpringBean
	private IUserService userService;
	
	private IModel<User> userModel;
	
	private IModel<String> passwordModel;
	
	private IModel<String> confirmPasswordModel;
	
	public RegisterFormPanel(String id, IModel<User> userModel) {
		super(id);
		
		this.userModel = userModel;
		this.passwordModel = Model.of();
		this.confirmPasswordModel = Model.of();
		
		Form<User> form = new Form<User>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				try {
					User user = RegisterFormPanel.this.userModel.getObject();
					String passwordValue = passwordModel.getObject();
					String confirmPasswordValue = confirmPasswordModel.getObject();
					String openIdIdentifierValue = user.getOpenIdIdentifier();
					
					user.setUserName(user.getEmail());
					if ((passwordValue != null && confirmPasswordValue != null) || openIdIdentifierValue != null) {
						List<User> usersWithSameName = userService.listByUserName(user.getUserName());
						
						if (usersWithSameName.isEmpty()) {
							userService.register(user, passwordValue);
							
							// Reset openId session attribute
							HttpServletRequest request = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
							request.getSession().removeAttribute(LinkUtils.OPENID_SESSION_CREDENTIALS);
							
							getSession().success(getString("register.success"));
							throw new RestartResponseException(HomePage.class);
						} else {
							LOGGER.warn("Username '" + user.getUserName() + "' already used");
							getSession().error(getString("register.userName.notUnique"));
						}
					}
				} catch (RestartResponseException e) {
					throw e;
				} catch (Exception e) {
					LOGGER.error("Error occured while creating account.", e);
					getSession().error(getString("register.error"));
				}
			}
		};
		
		// Email field
		EmailTextField emailInput = new EmailTextField("emailInput", BindingModel.of(userModel, Binding.user().email()));
		emailInput.setLabel(new ResourceModel("register.email"));
		emailInput.setRequired(true);
		form.add(emailInput);
		
		// Name fields
		TextField<String> fullNameInput = new TextField<String>("fullNameInput", BindingModel.of(userModel, Binding.user().fullName()));
		fullNameInput.setLabel(new ResourceModel("register.fullName"));
		form.add(fullNameInput);
		
		// Password fields
		WebMarkupContainer passwordContainer = new WebMarkupContainer("passwordContainer") {
			private static final long serialVersionUID = 2727669661139358058L;
			
			@SuppressWarnings("unchecked")
			@Override
			protected void onConfigure() {
				super.onConfigure();
				boolean isOpenIdRegistration = isOpenIdRegistration();
				
				setVisible(!isOpenIdRegistration);
				for (int i = 0; i < size(); ++i) {
					((FormComponent<String>) get(i)).setRequired(!isOpenIdRegistration);
				}
			}
		};
		form.add(passwordContainer);
		
		PasswordTextField passwordInput = new PasswordTextField("passwordInput", this.passwordModel); // FIXME: Génère un warning au submit
		passwordInput.setLabel(new ResourceModel("register.password"));
		passwordInput.add(new PasswordPatternValidator());
		passwordContainer.add(passwordInput);
		
		PasswordTextField confirmPasswordInput = new PasswordTextField("confirmPasswordInput", this.confirmPasswordModel);
		confirmPasswordInput.setLabel(new ResourceModel("register.confirmPassword"));
		passwordContainer.add(confirmPasswordInput);
		
		// OpenID field
		TextField<String> openIdIdentifierInput = new TextField<String>("openIdIdentifierInput",
				BindingModel.of(userModel, Binding.user().openIdIdentifier())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				boolean isOpenIdRegistration = isOpenIdRegistration();

				setVisible(isOpenIdRegistration);
				setRequired(isOpenIdRegistration);
			}
		};
		openIdIdentifierInput.setLabel(new ResourceModel("register.openId.identifier"));
		openIdIdentifierInput.setEnabled(false);
		form.add(openIdIdentifierInput);
		
		Link<Void> resetOpenIdLink = new Link<Void>("resetOpenIdLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				// Reset openId session attribute
				HttpServletRequest request = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
				request.getSession().removeAttribute(LinkUtils.OPENID_SESSION_CREDENTIALS);
				
				RegisterFormPanel.this.userModel.setObject(new User());
				RegisterFormPanel.this.userModel.getObject().setActive(false);
			}
		};
		form.add(resetOpenIdLink);
		
		form.add(new EqualPasswordInputValidator(passwordInput, confirmPasswordInput) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String resourceKey() {
				return "register.password.wrongConfirmation";
			}
		});
		form.add(new SubmitLink("submit"));
		
		add(form);
	}
	
	private boolean isOpenIdRegistration() {
		return userModel.getObject().getOpenIdIdentifier() != null;
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
