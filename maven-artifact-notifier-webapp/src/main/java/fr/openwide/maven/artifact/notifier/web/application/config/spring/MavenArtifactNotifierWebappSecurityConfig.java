package fr.openwide.maven.artifact.notifier.web.application.config.spring;

import com.google.inject.internal.ImmutableMap;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.core.config.spring.MavenArtifactNotifierCoreSecurityConfig;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service.Pac4jAuthenticationSuccessHandler;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service.Pac4jUserDetailsService;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils.Pac4jClient;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.context.support.ServletContextAttributeExporter;

@Configuration
@ImportResource({ "classpath:spring/security-web-context.xml" })
@Import(MavenArtifactNotifierCoreSecurityConfig.class)
public class MavenArtifactNotifierWebappSecurityConfig {
	
//	@Autowired
//	private MavenArtifactNotifierCoreSecurityConfig coreSecurityConfig;
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
//	@Autowired
//	private ApplicationContext applicationContext;
	
	@Bean
	public TwitterClient twitterClient() {
		final TwitterClient twitterClient = new TwitterClient(configurer.getTwitterClientKey(), configurer.getTwitterClientSecret());
		twitterClient.setCallbackUrl(configurer.getAuthenticationCallbackBaseUrl() + Pac4jAuthenticationUtils.CALLBACK_URI);
		return twitterClient;
	}
	
	@Bean
	public GitHubClient gitHubClient() {
		GitHubClient gitHubClient = new GitHubClient(configurer.getGitHubClientKey(), configurer.getGitHubClientSecret());
		gitHubClient.setScope(null);
		gitHubClient.setCallbackUrl(configurer.getAuthenticationCallbackBaseUrl() + Pac4jAuthenticationUtils.CALLBACK_URI);
		return gitHubClient;
	}
	
	@Bean
	public Google2Client googleClient() {
		Google2Client google2Client = new Google2Client(configurer.getGoogle2ClientKey(), configurer.getGoogle2ClientSecret());
		google2Client.setCallbackUrl(configurer.getAuthenticationCallbackBaseUrl() + Pac4jAuthenticationUtils.CALLBACK_URI);
		return google2Client;
	}

	@Bean
	public Clients clients() {
		return new Clients(configurer.getAuthenticationCallbackBaseUrl() + Pac4jAuthenticationUtils.CALLBACK_URI,
				googleClient(), gitHubClient(), twitterClient());
	}
	
	@Bean
	public ServletContextAttributeExporter servletContextAttributeExporter() {
		ServletContextAttributeExporter exporter = new ServletContextAttributeExporter();
		exporter.setAttributes(
				ImmutableMap.<String, Object>builder()
				.put(Pac4jClient.GOOGLE_OAUTH2.getClientKey(), googleClient())
				.put(Pac4jClient.GITHUB.getClientKey(), gitHubClient())
				.put(Pac4jClient.TWITTER.getClientKey(), twitterClient())
				.build()
		);
		return exporter;
	}
	
//	@Autowired
//	@Bean
//	public ClientAuthenticationFilter clientAuthenticationFilter(AuthenticationManager authenticationManager, Clients clients) {
//		ClientAuthenticationFilter filter = new ClientAuthenticationFilter("/" + Pac4jAuthenticationUtils.CALLBACK_URI);
//		filter.setClients(clients);
//		filter.setAuthenticationManager(authenticationManager);
//		filter.setAuthenticationFailureHandler(pac4jAuthenticationFailureHandler());
//		filter.setAuthenticationSuccessHandler(pac4jAuthenticationSuccessHandler());
//		return filter;
//	}

	@Bean
	public Config config(Clients clients) {
		final Config config = new Config(clients);
//		config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
//		config.addAuthorizer("custom", new CustomAuthorizer());
//		config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.html$"));
//		config.setCallbackLogic((context, config1, httpActionAdapter, defaultUrl, saveInSession, multiProfile, renewSession, client) -> {
//			System.out.println(client);
//		});
		return config;
	}

//	@Autowired
//	@Bean
//	public ClientAuthenticationProvider clientAuthenticationProvider(Clients clients) {
//		ClientAuthenticationProvider provider = new ClientAuthenticationProvider();
//		provider.setClients(clients);
//		return provider;
//	}
	
	@Bean
	public Pac4jUserDetailsService pac4jUserDetailsService() {
		return new Pac4jUserDetailsService();
	}
//
//	@Bean
//	public SimpleUrlAuthenticationFailureHandler pac4jAuthenticationFailureHandler() {
//		return new Pac4jAuthenticationFailureHandler();
//	}
//
	@Bean
	public SimpleUrlAuthenticationSuccessHandler pac4jAuthenticationSuccessHandler() {
		return new Pac4jAuthenticationSuccessHandler();
	}
//
//	@Bean(name = "authenticationService")
//	public IAuthenticationService authenticationService() {
//		return new Pac4jAuthenticationServiceImpl();
//	}
	
//	@PostConstruct
//	public void addPac4jToAuthenticationManager() {
//		applicationContext.getBean("authenticationManager", ProviderManager.class).getProviders()
//				.add(clientAuthenticationProvider(clients()));
//	}
}
