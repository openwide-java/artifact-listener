package fr.openwide.maven.artifact.notifier.web.application.navigation.component;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;

import fr.openwide.core.wicket.more.AbstractCoreSession;
import fr.openwide.core.wicket.more.markup.html.form.LabelPlaceholderBehavior;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ForgottenPasswordPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.LoginSuccessPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.RegisterPage;

public class HomeIdentificationPanel extends Panel {

	private static final long serialVersionUID = 4523289890652117373L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeIdentificationPanel.class);
	
	private EmailTextField userNameField;
	
	private PasswordTextField passwordField;
	
	public HomeIdentificationPanel(String id) {
		super(id);
		
		// Classic authentication
		Form<Void> signInForm = new StatelessForm<Void>("signInForm") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				AbstractCoreSession<?> session = AbstractCoreSession.get();
				boolean success = false;
				try {
					session.bind();
					session.signIn(userNameField.getModelObject(), passwordField.getModelObject());
					success = true;
				} catch (BadCredentialsException e) {
					session.error(getString("home.identification.classic.error.authentication"));
				} catch (UsernameNotFoundException e) {
					session.error(getString("home.identification.classic.error.authentication"));
				} catch (DisabledException e) {
					session.error(getString("home.identification.classic.error.userDisabled"));
				} catch (Exception e) {
					LOGGER.error("Erreur inconnue lors de l'authentification de l'utilisateur", e);
					session.error(getString("home.identification.classic.error.unknown"));
				}
				
				if (success) {
					throw new RestartResponseException(LoginSuccessPage.class);
				} else {
					setResponsePage(Application.get().getHomePage());
				}
			}
		};
		add(signInForm);
		
		userNameField = new EmailTextField("userName", Model.of(""));
		userNameField.setRequired(true);
		userNameField.setOutputMarkupId(true);
		userNameField.setLabel(new ResourceModel("home.identification.classic.email"));
		userNameField.add(new LabelPlaceholderBehavior());
		signInForm.add(userNameField);
		
		passwordField = new PasswordTextField("password", Model.of(""));
		passwordField.setLabel(new ResourceModel("home.identification.classic.password"));
		passwordField.add(new LabelPlaceholderBehavior());
		signInForm.add(passwordField);
		
		// Registration link
		signInForm.add(new BookmarkablePageLink<Void>("classicRegisterLink", RegisterPage.class));
		signInForm.add(new BookmarkablePageLink<Void>("forgottenPasswordLink", ForgottenPasswordPage.class));
		
		// OpenID authentication
		RequiredTextField<String> openIdIdentifierField = new RequiredTextField<String>("openIdIdentifier", Model.of("")) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getInputName() {
				return "openid_identifier";
			};
		};
		openIdIdentifierField.setLabel(new ResourceModel("home.identification.openId.label"));
		openIdIdentifierField.add(new LabelPlaceholderBehavior());
		add(openIdIdentifierField);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		// Vérification des retours d'auth OpenID
		HttpServletRequest request = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
		AuthenticationException exception = (AuthenticationException) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (exception != null) {
			if (exception instanceof DisabledException) {
				getSession().error(getString("home.identification.classic.error.userDisabled"));
			} else if (exception instanceof AuthenticationServiceException) {
				LOGGER.error("Authentication failed", exception);
				getSession().error(getString("home.identification.error.badCredentials") + exception.getMessage());
			} else {
				LOGGER.error("An unknown error occurred during the authentication process", exception);
				getSession().error(getString("home.identification.error.unknown"));
			}
			request.getSession().removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(!AuthenticatedWebSession.exists() || !AuthenticatedWebSession.get().isSignedIn());
	}
}
