package fr.openwide.maven.artifact.notifier.web.application.notification.service;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.wicket.more.link.descriptor.generator.IPageLinkGenerator;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.core.wicket.more.notification.service.AbstractNotificationUrlBuilderServiceImpl;
import fr.openwide.core.wicket.more.notification.service.IWicketContextExecutor;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationUrlBuilderService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.AboutPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ViewProfilePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmEmailNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmRegistrationNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.DeleteEmailNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ResetPasswordNotificationPage;

@Service("webappNotificationUrlBuilderService")
public class WebappNotificationUrlBuilderServiceImpl extends AbstractNotificationUrlBuilderServiceImpl implements INotificationUrlBuilderService {
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Autowired
	public WebappNotificationUrlBuilderServiceImpl(IWicketContextExecutor wicketExecutor) {
		super(wicketExecutor);
	}
	
	@Override
	public String getHomeUrl() {
		Callable<IPageLinkGenerator> pageLinkGeneratorTask = new Callable<IPageLinkGenerator>() {
			@Override
			public IPageLinkGenerator call() throws Exception {
				return HomePage.linkDescriptor();
			}
		};
		return buildUrl(pageLinkGeneratorTask);
	}
	
	@Override
	public String getAboutUrl() {
		Callable<IPageLinkGenerator> pageLinkGeneratorTask = new Callable<IPageLinkGenerator>() {
			@Override
			public IPageLinkGenerator call() throws Exception {
				return AboutPage.linkDescriptor();
			}
		};
		return buildUrl(pageLinkGeneratorTask);
	}
	
	@Override
	public String getGitHubUrl() {
		return configurer.getLinkGitHubProject();
	}
	
	@Override
	public String getProfileUrl() {
		Callable<IPageLinkGenerator> pageLinkGeneratorTask = new Callable<IPageLinkGenerator>() {
			@Override
			public IPageLinkGenerator call() throws Exception {
				return ViewProfilePage.linkDescriptor();
			}
		};
		return buildUrl(pageLinkGeneratorTask);
	}
	
	@Override
	public String getConfirmRegistrationUrl(User user) {
		return buildUrl(ConfirmRegistrationNotificationPage.class, LinkUtils.getUserHashPageParameters(user));
	}
	
	@Override
	public String getResetPasswordUrl(User user) {
		return buildUrl(ResetPasswordNotificationPage.class, LinkUtils.getUserHashPageParameters(user));
	}
	
	@Override
	public String getConfirmEmailUrl(EmailAddress emailAddress) {
		return buildUrl(ConfirmEmailNotificationPage.class, LinkUtils.getEmailHashPageParameters(emailAddress));
	}
	
	@Override
	public String getDeleteEmailUrl(EmailAddress emailAddress) {
		return buildUrl(DeleteEmailNotificationPage.class, LinkUtils.getEmailHashPageParameters(emailAddress));
	}
	
	@Override
	public String getArtifactDescriptionUrl(final Artifact artifact) {
		Callable<IPageLinkGenerator> pageLinkGeneratorTask = new Callable<IPageLinkGenerator>() {
			@Override
			public IPageLinkGenerator call() throws Exception {
				return ArtifactDescriptionPage.linkDescriptor(new GenericEntityModel<Long, Artifact>(artifact));
			}
		};
		return buildUrl(pageLinkGeneratorTask);
	}
}
