package fr.openwide.maven.artifact.notifier.web.application.config.spring;

import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service.Pac4jUserDetailsService;
import org.apache.commons.collections4.CollectionUtils;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.springframework.security.authentication.Pac4jAuthenticationToken;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;
import org.pac4j.springframework.security.web.CallbackFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomCallbackFilter extends CallbackFilter {
	@Autowired
	private Pac4jUserDetailsService pac4jUserDetailsService;

	private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		super.doFilter(req, resp, chain);

//		Pac4jAuthenticationToken tokenWithUserDetails = getAuthenticationTokenWithUserDetails(authentication);
//		SecurityContextHolder.getContext().setAuthentication(tokenWithUserDetails);

		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) resp;
		WebContext context = new JEEContext(request, response);
		SpringSecurityProfileManager manager = new SpringSecurityProfileManager(context);
		List<CommonProfile> profiles = manager.getAll(true);
		if (CollectionUtils.isNotEmpty(profiles)) {
			final Pac4jAuthenticationToken token = new Pac4jAuthenticationToken(profiles);

			UserDetails userDetails = null;
			try {
				userDetails = pac4jUserDetailsService.loadUserDetails(token);
			} catch (Exception e) {
				// FIXME
				logger.error("FIXME", e);
			}

			Set<String> authorities = new HashSet<>(Collections.singletonList("ROLE_ANONYMOUS"));
			if (userDetails != null) {
				this.userDetailsChecker.check(userDetails);
				authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
			}
			final CommonProfile profile = token.getProfile();
			profile.setRoles(authorities);
			Pac4jAuthenticationToken result =  new Pac4jAuthenticationToken(Collections.singletonList(profile));
			result.setDetails(userDetails);

			SecurityContextHolder.getContext().setAuthentication(result);
		}
	}
}
