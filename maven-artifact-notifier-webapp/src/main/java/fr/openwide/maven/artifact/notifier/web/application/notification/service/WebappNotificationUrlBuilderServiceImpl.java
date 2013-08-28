package fr.openwide.maven.artifact.notifier.web.application.notification.service;

import org.apache.wicket.Page;
import org.apache.wicket.core.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationUrlBuilderService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.AboutPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ViewProfilePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmEmailNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmRegistrationNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.DeleteEmailNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ResetPasswordNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.util.AbstractDummyThreadContextBuilder;

@Service("webappNotificationUrlBuilderService")
public class WebappNotificationUrlBuilderServiceImpl extends AbstractDummyThreadContextBuilder implements INotificationUrlBuilderService {
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Override
	public String getHomeUrl() {
		return buildUrl(MavenArtifactNotifierApplication.NAME, HomePage.class, null);
	}
	
	@Override
	public String getAboutUrl() {
		return buildUrl(MavenArtifactNotifierApplication.NAME, AboutPage.class, null);
	}
	
	@Override
	public String getGitHubUrl() {
		return configurer.getLinkGitHubProject();
	}
	
	@Override
	public String getProfileUrl() {
		return buildUrl(MavenArtifactNotifierApplication.NAME, ViewProfilePage.class, null);
	}
	
	@Override
	public String getConfirmRegistrationUrl(User user) {
		return buildUrl(MavenArtifactNotifierApplication.NAME, ConfirmRegistrationNotificationPage.class, LinkUtils.getUserHashPageParameters(user));
	}
	
	@Override
	public String getResetPasswordUrl(User user) {
		return buildUrl(MavenArtifactNotifierApplication.NAME, ResetPasswordNotificationPage.class, LinkUtils.getUserHashPageParameters(user));
	}
	
	@Override
	public String getConfirmEmailUrl(EmailAddress emailAddress) {
		return buildUrl(MavenArtifactNotifierApplication.NAME, ConfirmEmailNotificationPage.class, LinkUtils.getEmailHashPageParameters(emailAddress));
	}
	
	@Override
	public String getDeleteEmailUrl(EmailAddress emailAddress) {
		return buildUrl(MavenArtifactNotifierApplication.NAME, DeleteEmailNotificationPage.class, LinkUtils.getEmailHashPageParameters(emailAddress));
	}
	
	@Override
	public String getArtifactDescriptionUrl(Artifact artifact) {
		return ArtifactDescriptionPage.linkDescriptor(Model.of(artifact)).fullUrl(getRequestCycle(MavenArtifactNotifierApplication.NAME));
	}
	
	private String buildUrl(String applicationName, Class<? extends Page> pageClass, PageParameters parameters) {
		return buildUrl(applicationName, new BookmarkablePageRequestHandler(new PageProvider(pageClass, parameters)));
	}
	
	private String buildUrl(String applicationName, IRequestHandler requestHandler) {
		Args.notNull(applicationName, "applicationName");
		Args.notNull(requestHandler, "requestHandler");
		
		RequestCycle requestCycle = getRequestCycle(applicationName);
		String url = requestCycle.getUrlRenderer().renderFullUrl(Url.parse(requestCycle.urlFor(requestHandler)));
		detachDummyThreadContext();
		return url;
	}
}
