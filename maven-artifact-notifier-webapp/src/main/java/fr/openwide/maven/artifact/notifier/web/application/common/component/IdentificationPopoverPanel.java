package fr.openwide.maven.artifact.notifier.web.application.common.component;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsUtils;
import org.pac4j.openid.client.MyOpenIdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;

import fr.openwide.core.wicket.more.AbstractCoreSession;
import fr.openwide.core.wicket.more.markup.html.form.LabelPlaceholderBehavior;
import fr.openwide.core.wicket.more.request.cycle.RequestCycleUtils;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils.Pac4jClient;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ForgottenPasswordPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.LoginSuccessPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.RegisterPage;

public class IdentificationPopoverPanel extends Panel {

	private static final long serialVersionUID = 4523289890652117373L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentificationPopoverPanel.class);
	
	private EmailTextField userNameField;
	
	private PasswordTextField passwordField;
	
	public IdentificationPopoverPanel(String id) {
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
					session.error(getString("home.identification.error.unknown"));
				}
				
				if (success) {
					if (HomePage.class.equals(getPage().getClass()) || RegisterPage.class.equals(getPage().getClass())) {
						throw new RestartResponseException(LoginSuccessPage.class);
					} else {
						throw new RestartResponseException(getPage().getClass(), getPage().getPageParameters());
					}
				} else {
					setResponsePage(getPage());
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
		
		// Google authentication
		StatelessForm<Void> googleOpenIdForm = new StatelessForm<Void>("googleOpenIdForm") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected CharSequence getActionUrl() {
				return Pac4jAuthenticationUtils.getClientRedirectUrl(Pac4jClient.GOOGLE);
			}
		};
		add(googleOpenIdForm);
		
		// Twitter authentication
		add(new ExternalLink("twitterLink", Pac4jAuthenticationUtils.getClientRedirectUrl(Pac4jClient.TWITTER)));
		
		// GitHub authentication
		add(new ExternalLink("gitHubLink", Pac4jAuthenticationUtils.getClientRedirectUrl(Pac4jClient.GITHUB)));
		
		// MyOpenID authentication
		StatelessForm<Void> myOpenIdForm = new StatelessForm<Void>("myOpenIdForm") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected CharSequence getActionUrl() {
				return Pac4jAuthenticationUtils.getClientRedirectUrl(Pac4jClient.MYOPENID);
			}
		};
		add(myOpenIdForm);
		
		RequiredTextField<String> openIdIdentifierField = new RequiredTextField<String>("openIdIdentifier", Model.of("")) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getInputName() {
				return MyOpenIdClient.DEFAULT_USER_PARAMETER_NAME;
			};
		};
		openIdIdentifierField.setLabel(new ResourceModel("home.identification.openId.label"));
		openIdIdentifierField.add(new LabelPlaceholderBehavior());
		openIdIdentifierField.add(new AttributeModifier("pattern", Pac4jAuthenticationUtils.MYOPENID_IDENTIFIER_PATTERN));
		myOpenIdForm.add(openIdIdentifierField);
		
		add(new ShowIdentificationPopoverBehavior());
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		// Vérification des retours d'auth pac4J
		HttpServletRequest request = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
		Exception exception = (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
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
	
	private class ShowIdentificationPopoverBehavior extends Behavior {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void renderHead(Component component, IHeaderResponse response) {
			boolean redirectedByWicket = MavenArtifactNotifierSession.get().getRedirectUrl() != null;
			boolean redirectedBySpringSecurity = RequestCycleUtils.getCurrentContainerRequest().getSession()
					.getAttribute(MavenArtifactNotifierSession.SPRING_SECURITY_SAVED_REQUEST) != null;
			boolean hasFeedbackMessages = getSession().getFeedbackMessages().hasMessage(FeedbackMessage.ERROR) || 
					userNameField.getFeedbackMessages().hasMessage(FeedbackMessage.ERROR);
			
			// FIXME: The popover will show if the SPRING_SECURITY_SAVED_REQUEST attribute is present in the session
			// If such is the case the popover will show every time while navigating on public pages
			if (redirectedByWicket || redirectedBySpringSecurity || hasFeedbackMessages) {
				CharSequence showIdentificationPopover = new JsQuery().$(".popover-btn").chain("popover", JsUtils.quotes("show")).render();
				response.render(OnDomReadyHeaderItem.forScript(showIdentificationPopover));
			}
		}
	}
}
