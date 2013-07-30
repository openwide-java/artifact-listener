package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.exception.TechnicalException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.web.filter.GenericFilterBean;

import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;

public class Pac4jExceptionFilter extends GenericFilterBean {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		try {
			chain.doFilter(request, response);
		} catch (final TechnicalException e) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			
			httpRequest.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, e);
			getRedirectStrategy().sendRedirect(httpRequest, httpResponse, Pac4jAuthenticationUtils.LOGIN_FAILURE_URL);
		}
	}
	
	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}
}
