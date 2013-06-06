package fr.openwide.maven.artifact.notifier.web.application.auth.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;
import fr.openwide.maven.artifact.notifier.web.application.auth.model.NormalizedOpenIdAttributes;
import fr.openwide.maven.artifact.notifier.web.application.auth.model.NormalizedOpenIdAttributesBuilder;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class OpenIdAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	
	private static final String REGISTER_PAGE_URL = MavenArtifactNotifierApplication.REGISTER_URL;
	private static final String HOME_PAGE_URL = "/";
	
	private NormalizedOpenIdAttributesBuilder normalizedOpenIdAttributesBuilder;
	
	public OpenIdAuthenticationFailureHandler(NormalizedOpenIdAttributesBuilder normalizedOpenIdAttributesBuilder) {
		super(HOME_PAGE_URL);
		this.normalizedOpenIdAttributesBuilder = normalizedOpenIdAttributesBuilder;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (isAuthSuccessfullOnUnregisteredUser(exception)) {
			addOpenIdAttributesToSession(request, getOpenIdAuthenticationToken(exception));
			getRedirectStrategy().sendRedirect(request, response, REGISTER_PAGE_URL);
			
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}

	private void addOpenIdAttributesToSession(HttpServletRequest request,
			OpenIDAuthenticationToken openIdAuthenticationToken) throws ServletException {
		NormalizedOpenIdAttributes normalizedOpenIdAttributes = normalizedOpenIdAttributesBuilder
				.build(openIdAuthenticationToken);
		getHttpSession(request).setAttribute(LinkUtils.OPENID_SESSION_CREDENTIALS, normalizedOpenIdAttributes);
	}
	
	private HttpSession getHttpSession(HttpServletRequest request) throws ServletException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new ServletException("No session found");
		}
		return session;
	}
	
	private boolean isAuthSuccessfullOnUnregisteredUser(AuthenticationException exception) {
		return exception instanceof UsernameNotFoundException && isAuthSuccessful(exception);
	}
	
	@SuppressWarnings("deprecation")
	private boolean isAuthSuccessful(AuthenticationException exception) {
		return exception.getAuthentication() instanceof OpenIDAuthenticationToken
				&& OpenIDAuthenticationStatus.SUCCESS.equals((getOpenIdAuthenticationToken(exception)).getStatus());
	}

	@SuppressWarnings("deprecation")
	private OpenIDAuthenticationToken getOpenIdAuthenticationToken(AuthenticationException exception) {
		return ((OpenIDAuthenticationToken) exception.getAuthentication());
	}
}