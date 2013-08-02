package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;

public class Pac4jAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	
	public Pac4jAuthenticationFailureHandler() {
		super(Pac4jAuthenticationUtils.LOGIN_FAILURE_URL);
	}
}