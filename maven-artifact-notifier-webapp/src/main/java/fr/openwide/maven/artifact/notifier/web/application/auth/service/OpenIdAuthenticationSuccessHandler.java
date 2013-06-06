package fr.openwide.maven.artifact.notifier.web.application.auth.service;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;

public class OpenIdAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	public OpenIdAuthenticationSuccessHandler() {
		super(MavenArtifactNotifierApplication.OPENID_LOGIN_SUCCESS_URL);
		setAlwaysUseDefaultTargetUrl(true);
	}
}