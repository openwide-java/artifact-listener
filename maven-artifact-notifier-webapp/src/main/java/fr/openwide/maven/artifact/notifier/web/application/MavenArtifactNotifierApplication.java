package fr.openwide.maven.artifact.notifier.web.application;

import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.devutils.stateless.StatelessChecker;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

import fr.openwide.core.wicket.more.application.CoreWicketAuthenticatedApplication;
import fr.openwide.core.wicket.more.console.common.model.ConsoleMenuItem;
import fr.openwide.core.wicket.more.console.common.model.ConsoleMenuSection;
import fr.openwide.core.wicket.more.console.template.ConsoleConfiguration;
import fr.openwide.core.wicket.more.link.descriptor.parameter.CommonParameters;
import fr.openwide.core.wicket.more.markup.html.pages.monitoring.DatabaseMonitoringPage;
import fr.openwide.core.wicket.more.security.page.LoginFailurePage;
import fr.openwide.core.wicket.more.security.page.LoginSuccessPage;
import fr.openwide.core.wicket.more.security.page.LogoutPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationArtifactPortfolioPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserGroupDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserGroupPortfolioPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserPortfolioPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactPomSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.console.importation.page.ConsoleImportProjectPage;
import fr.openwide.maven.artifact.notifier.web.application.console.notification.page.ConsoleNotificationIndexPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.AboutPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.DashboardPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ForgottenPasswordPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.Pac4jLoginSuccessPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.RegisterPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ViewProfilePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmEmailNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ConfirmRegistrationNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.DeleteEmailNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.notification.page.ResetPasswordNotificationPage;
import fr.openwide.maven.artifact.notifier.web.application.project.page.ProjectDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.project.page.ProjectListPage;

public class MavenArtifactNotifierApplication extends CoreWicketAuthenticatedApplication {
	
	/**
	 * This is the name used in the filter definition. Wicket uses the filter name to set the name of the application.
	 */
	public static final String NAME = "MavenArtifactNotifierApplication";
	
	public static final String REGISTER_URL = "/register/";
	public static final String PAC4J_LOGIN_SUCCESS_URL = "/login/pac4j/success/";
	
	@Override
	public void init() {
		super.init();
		
		if (RuntimeConfigurationType.DEVELOPMENT.equals(getConfigurationType())) {
			getComponentPostOnBeforeRenderListeners().add(new StatelessChecker());
		}
		
		if (RuntimeConfigurationType.DEPLOYMENT.equals(getConfigurationType())) {
			addResourceReplacement(JQueryResourceReference.get(),
					new UrlResourceReference(Url.parse("//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js")));
		}
	}

	@Override
	protected void mountApplicationPages() {
		// Public
		mountUnversionedPage("/about/", AboutPage.class);
		mountPage(REGISTER_URL, RegisterPage.class);
		mountPage("/forgotten-password/", ForgottenPasswordPage.class);
		
		// Sign in
		mountPage("/login/failure/", LoginFailurePage.class);
		mountPage("/login/success/", LoginSuccessPage.class);
		mountPage(PAC4J_LOGIN_SUCCESS_URL, Pac4jLoginSuccessPage.class);
		
		// Dashboard
		mountPage("/dashboard/", DashboardPage.class);
		
		// Profile
		mountPage("/profile/", ViewProfilePage.class);
		
		// Artifact
		mountPage("/search/", ArtifactSearchPage.class);
		mountPage("/search/pom/", ArtifactPomSearchPage.class);
		mountParameterizedPage("/artifact/${" + CommonParameters.NATURAL_ID + "}/", ArtifactDescriptionPage.class);
		
		// Project
		mountPage("/projects/", ProjectListPage.class);
		mountParameterizedPage("/project/${" + CommonParameters.NATURAL_ID + "}/", ProjectDescriptionPage.class);
		
		// Notification
		mountParameterizedPage("/notification/account/confirm/${" + LinkUtils.HASH_PARAMETER + "}/", ConfirmRegistrationNotificationPage.class);
		mountParameterizedPage("/notification/account/reset/${" + LinkUtils.HASH_PARAMETER + "}/", ResetPasswordNotificationPage.class);
		mountParameterizedPage("/notification/email/confirm/${" + LinkUtils.HASH_PARAMETER + "}/", ConfirmEmailNotificationPage.class);
		mountParameterizedPage("/notification/email/delete/${" + LinkUtils.HASH_PARAMETER + "}/", DeleteEmailNotificationPage.class);
		
		// Administration
		mountPage("/administration/user/", AdministrationUserPortfolioPage.class);
		mountParameterizedPage("/administration/user/${" + CommonParameters.ID + "}/", AdministrationUserDescriptionPage.class);
		mountPage("/administration/user-group/", AdministrationUserGroupPortfolioPage.class);
		mountParameterizedPage("/administration/user-group/${" + CommonParameters.ID + "}/", AdministrationUserGroupDescriptionPage.class);
		mountPage("/administration/artifact/", AdministrationArtifactPortfolioPage.class);
		mountParameterizedPage("/administration/artifact/${" + CommonParameters.NATURAL_ID + "}/",
				AdministrationArtifactDescriptionPage.class);
		
		// Console
		ConsoleConfiguration consoleConfiguration = ConsoleConfiguration.build("console");
		consoleConfiguration.addMenuSection(new ConsoleMenuSection("notificationMenuSection", "console.notifications",
				"notifications", ConsoleNotificationIndexPage.class));
		
		ConsoleMenuSection importMenuSection = new ConsoleMenuSection("importMenuSection", "console.import", "import", ConsoleImportProjectPage.class);
		importMenuSection.addMenuItem(new ConsoleMenuItem("importProjectMenuItem", "console.import.project", "project", ConsoleImportProjectPage.class));
		consoleConfiguration.addMenuSection(importMenuSection);
		consoleConfiguration.mountPages(this);
		
		// Monitoring
		mountPage("/monitoring/db-access/", DatabaseMonitoringPage.class);
	}
	
	@Override
	protected void mountCommonPages() {
		mountPage("/logout/", LogoutPage.class);
	}

	@Override
	protected void mountApplicationResources() {
		mountStaticResourceDirectory("/application", MainTemplate.class);
		mountStaticResourceDirectory("/console/import/project", ConsoleImportProjectPage.class);
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return MavenArtifactNotifierSession.class;
	}

	@Override
	public Class<? extends WebPage> getSignInPageClass() {
		return HomePage.class;
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}
}
