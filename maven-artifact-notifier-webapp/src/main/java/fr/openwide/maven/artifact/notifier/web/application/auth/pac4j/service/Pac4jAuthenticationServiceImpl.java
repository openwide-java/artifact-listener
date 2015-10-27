package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import fr.openwide.core.jpa.security.service.CoreAuthenticationServiceImpl;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;

public class Pac4jAuthenticationServiceImpl extends CoreAuthenticationServiceImpl {

	@Override
	public String getUserName() {
		return Pac4jAuthenticationUtils.getUserName();
	}
	
	@Override
	public boolean isLoggedIn() {
		return Pac4jAuthenticationUtils.isLoggedIn();
	}
	
	@Override
	public List<? extends GrantedAuthority> getAuthorities() {
		return Pac4jAuthenticationUtils.getAuthorities();
	}
	
	@Override
	public Authentication getAuthentication() {
		return Pac4jAuthenticationUtils.getAuthentication();
	}

	@Override
	public void signOut() {
		Pac4jAuthenticationUtils.setAuthentication(null);
	}
	
}
