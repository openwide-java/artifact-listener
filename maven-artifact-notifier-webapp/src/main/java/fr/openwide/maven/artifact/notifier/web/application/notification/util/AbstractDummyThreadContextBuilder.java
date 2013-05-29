package fr.openwide.maven.artifact.notifier.web.application.notification.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

public abstract class AbstractDummyThreadContextBuilder {
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	private boolean isDummyThreadContext = false;
	
	protected WebApplication getWebApplication(String applicationName) {
		if (!WebApplication.exists()) {
			createDummyThreadContext(applicationName);
		}
		return WebApplication.get();
	}
	
	protected RequestCycle getRequestCycle(String applicationName) {
		if (RequestCycle.get() == null) {
			createDummyThreadContext(applicationName);
		}
		return RequestCycle.get();
	}
	
	protected Session getSession(String applicationName) {
		getRequestCycle(applicationName);
		return Session.get();
	}
	
	protected void detachDummyThreadContext() {
		if (isDummyThreadContext) {
			ThreadContext.detach();
			isDummyThreadContext = false;
		}
	}
	
	private void createDummyThreadContext(String applicationName) {
		WebApplication webApplication = retrieveWebApplication(applicationName);
		RequestCycle requestCycle = createDummyRequestCycle(webApplication);

		ThreadContext.setApplication(webApplication);
		ThreadContext.setRequestCycle(requestCycle);
		isDummyThreadContext = true;
	}
	
	private WebApplication retrieveWebApplication(String applicationName) {
		WebApplication webApplication;
		if (WebApplication.exists()) {
			webApplication = WebApplication.get();
		} else {
			Application application = WebApplication.get(applicationName);
			if (!(application instanceof WebApplication)) {
				throw new IllegalStateException("Application is not a WebApplication");
			}
			webApplication = (WebApplication) application;
		}
		return webApplication;
	}
	
	private RequestCycle createDummyRequestCycle(WebApplication application) {
		final ServletContext context = application.getServletContext();
		
		final HttpSession newHttpSession = new MockHttpSession(context);
		final MockHttpServletRequest servletRequest = new MockHttpServletRequest(application, newHttpSession, context) {
			
			@Override
			public String getServerName() {
				return configurer.getDummyThreadContextServerName();
			}
			
			@Override
			public int getServerPort() {
				return configurer.getDummyThreadContextServerPort();
			}

			@Override
			public String getScheme() {
				return configurer.getDummyThreadContextScheme();
			}
			
			@Override
			public String getContextPath() {
				return context.getContextPath();
			}
			
			@Override
			public String getServletPath() {
				return "/";
			}
		};
		final MockHttpServletResponse servletResponse = new MockHttpServletResponse(servletRequest);
		servletRequest.initialize();
		servletResponse.initialize();
		
		final ServletWebRequest webRequest = new ServletWebRequest(servletRequest, servletRequest.getFilterPrefix());
		final WebResponse webResponse = new BufferedWebResponse(new ServletWebResponse(webRequest, servletResponse));
		
		return application.createRequestCycle(webRequest, webResponse);
	}
}
