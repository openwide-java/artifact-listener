package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.oauth.profile.github.GitHubProfile;
import org.pac4j.oauth.profile.twitter.TwitterProfile;
import org.pac4j.openid.profile.google.GoogleOpenIdProfile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.collect.Lists;

import fr.openwide.maven.artifact.notifier.core.business.user.model.AuthenticationType;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;

public final class Pac4jAuthenticationUtils {

	public enum Pac4jClient {
		TWITTER("TwitterClient"),
		GITHUB("GitHubClient"),
		GOOGLE("GoogleOpenIdClient"),
		MYOPENID("MyOpenIdClient");

		private String clientKey;
		
		private Pac4jClient(String clientKey) {
			this.clientKey = clientKey;
		}
		
		public String getClientKey() {
			return clientKey;
		}
	}
	
	public static final String AUTH_TOKEN_ATTRIBUTE = "authenticationTokenAttribute";
	
	public static final String CALLBACK_URI = "login/callback";
	
	public static final String LOGIN_SUCCESS_URL = MavenArtifactNotifierApplication.PAC4J_LOGIN_SUCCESS_URL;
	
	public static final String LOGIN_FAILURE_URL = "/";
	
	public static final String REGISTER_URL = MavenArtifactNotifierApplication.REGISTER_URL;
	
	public static final String MYOPENID_IDENTIFIER_PATTERN = "http://\\w+\\.myopenid\\.com/?";
	
	public static AuthenticationType getAuthenticationType(Authentication authentication) {
		if (authentication != null && authentication instanceof ClientAuthenticationToken) {
			ClientAuthenticationToken token = (ClientAuthenticationToken) authentication;
			if (token.getUserProfile() instanceof GoogleOpenIdProfile) {
				return AuthenticationType.OPENID_GOOGLE;
			} else if (token.getUserProfile() instanceof TwitterProfile) {
				return AuthenticationType.TWITTER;
			} else if (token.getUserProfile() instanceof GitHubProfile) {
				return AuthenticationType.GITHUB;
			} else {
				throw new IllegalStateException("Invalid user profile type");
			}
		}
		return AuthenticationType.LOCAL;
	}
	
	public static String getClientRedirectUrl(Pac4jClient client) {
		BaseClient<?, ?> baseClient = (BaseClient<?, ?>) WebApplication.get().getServletContext().getAttribute(client.getClientKey());
		HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
		HttpServletResponse response = (HttpServletResponse) RequestCycle.get().getResponse().getContainerResponse();
		
		return baseClient.getRedirectionUrl(new J2EContext(request, response));
	}
	
	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	public static void setAuthentication(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	public static boolean isLoggedIn() {
		return (getUserName() != null);
	}
	
	public static String getUserName() {
		UserDetails details = null;
		
		Authentication authentication = getAuthentication();
		if (authentication != null) {
			if (authentication.getDetails() instanceof UserDetails) {
				details = (UserDetails) authentication.getDetails();
			} else if (authentication.getPrincipal() instanceof UserDetails) {
				details = (UserDetails) authentication.getPrincipal();
			}
		}

		return details != null ? details.getUsername() : null;
	}
	
	public static List<? extends GrantedAuthority> getAuthorities() {
		return Lists.newArrayList(getAuthentication().getAuthorities());
	}
	
	private Pac4jAuthenticationUtils() {
	}
}
