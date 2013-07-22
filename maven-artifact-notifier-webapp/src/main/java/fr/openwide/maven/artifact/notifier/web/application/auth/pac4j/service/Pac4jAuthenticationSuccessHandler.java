package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;

public class Pac4jAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Autowired
	private Pac4jUserDetailsService pac4jUserDetailsService;
	
	private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
	
	public Pac4jAuthenticationSuccessHandler() {
		super();
		setDefaultTargetUrl(Pac4jAuthenticationUtils.LOGIN_SUCCESS_URL);
		setAlwaysUseDefaultTargetUrl(true);
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		String targetUrl;
		try {
			ClientAuthenticationToken tokenWithUserDetails = getAuthenticationTokenWithUserDetails(authentication);
			SecurityContextHolder.getContext().setAuthentication(tokenWithUserDetails);
			
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		} catch (UsernameNotFoundException e) {
			saveAuthentication(request, getAuthenticationToken(authentication));
			targetUrl = Pac4jAuthenticationUtils.REGISTER_URL;
		} catch (AuthenticationException e) {
			saveException(request, e);
			targetUrl = Pac4jAuthenticationUtils.LOGIN_FAILURE_URL;
		}
		// Failed to retrieve user details
		SecurityContextHolder.clearContext();
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
	
	private final void saveAuthentication(HttpServletRequest request, ClientAuthenticationToken token) {
		request.getSession().setAttribute(Pac4jAuthenticationUtils.AUTH_TOKEN_ATTRIBUTE, token);
	}
	
	private final void saveException(HttpServletRequest request, AuthenticationException exception) {
		request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
	}
	
	private ClientAuthenticationToken getAuthenticationTokenWithUserDetails(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		ClientAuthenticationToken token = getAuthenticationToken(authentication);
		
		if (token != null) {
			UserDetails userDetails = pac4jUserDetailsService.loadUserDetails(token);
			
			if (userDetails != null) {
				this.userDetailsChecker.check(userDetails);
				authorities = userDetails.getAuthorities();
			}
			ClientAuthenticationToken result =  new ClientAuthenticationToken((Credentials) token.getCredentials(),
					token.getClientName(), token.getUserProfile(), authorities);
			result.setDetails(userDetails);
			return result;
		}
		return null;
	}
	
	private ClientAuthenticationToken getAuthenticationToken(Authentication authentication) {
		return authentication instanceof ClientAuthenticationToken ? (ClientAuthenticationToken) authentication : null;
	}
}