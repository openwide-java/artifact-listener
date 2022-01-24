package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util;

import com.google.common.collect.Lists;
import fr.openwide.maven.artifact.notifier.core.business.user.model.AuthenticationType;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oauth.profile.github.GitHubProfile;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.oauth.profile.twitter.TwitterProfile;
import org.pac4j.springframework.security.authentication.Pac4jAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public final class Pac4jAuthenticationUtils {

	public enum Pac4jClient {
		TWITTER("TwitterClient"),
		GITHUB("GitHubClient"),
		GOOGLE_OAUTH2("Google2Client"),
		MYOPENID("MyOpenIdClient");

		private final String clientKey;
		
		Pac4jClient(String clientKey) {
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
	
	public static AuthenticationType getAuthenticationType(Authentication authentication) {
		if (authentication instanceof Pac4jAuthenticationToken) {
			Pac4jAuthenticationToken token = (Pac4jAuthenticationToken) authentication;
			final CommonProfile profile = token.getProfile();
			if (profile instanceof Google2Profile) {
				return AuthenticationType.OAUTH2_GOOGLE;
			} else if (profile instanceof TwitterProfile) {
				return AuthenticationType.TWITTER;
			} else if (profile instanceof GitHubProfile) {
				return AuthenticationType.GITHUB;
			} else {
				throw new IllegalStateException("Invalid user profile type");
			}
		}
		return AuthenticationType.LOCAL;
	}
	
	public static String getClientRedirectUrl(Pac4jClient client) {
		BaseClient<?> baseClient = (BaseClient<?>) WebApplication.get().getServletContext().getAttribute(client.getClientKey());
		HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
		HttpServletResponse response = (HttpServletResponse) RequestCycle.get().getResponse().getContainerResponse();

		return baseClient.getRedirectionAction(new JEEContext(request, response))
				.filter(ra -> ra instanceof WithLocationAction)
				.map(ra -> ((WithLocationAction) ra).getLocation())
				.orElse("");
//		return baseClient.getRedirectionUrl(new JEEContext(request, response));
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
