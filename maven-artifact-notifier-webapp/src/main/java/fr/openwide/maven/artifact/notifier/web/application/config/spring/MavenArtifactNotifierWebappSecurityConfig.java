package fr.openwide.maven.artifact.notifier.web.application.config.spring;

import javax.annotation.PostConstruct;

import org.pac4j.core.client.Clients;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.openid.client.GoogleOpenIdClient;
import org.pac4j.openid.client.MyOpenIdClient;
import org.pac4j.springframework.security.authentication.ClientAuthenticationProvider;
import org.pac4j.springframework.security.web.ClientAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.context.support.ServletContextAttributeExporter;

import com.google.inject.internal.ImmutableMap;

import fr.openwide.core.jpa.security.service.IAuthenticationService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.core.config.spring.MavenArtifactNotifierCoreSecurityConfig;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service.Pac4jAuthenticationFailureHandler;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service.Pac4jAuthenticationServiceImpl;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service.Pac4jAuthenticationSuccessHandler;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service.Pac4jUserDetailsService;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils.Pac4jClient;

@Configuration
@ImportResource({ "classpath:spring/security-web-context.xml" })
@Import(MavenArtifactNotifierCoreSecurityConfig.class)
public class MavenArtifactNotifierWebappSecurityConfig {
	
	@Autowired
	private MavenArtifactNotifierCoreSecurityConfig coreSecurityConfig;
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Autowired
	private ProviderManager authenticationManager;
	
	@Bean
	public TwitterClient twitterClient() {
		return new TwitterClient(configurer.getTwitterClientKey(), configurer.getTwitterClientSecret());
	}
	
	@Bean
	public GitHubClient gitHubClient() {
		return new GitHubClient(configurer.getGitHubClientKey(), configurer.getGitHubClientSecret());
	}
	
	@Bean
	public GoogleOpenIdClient googleClient() {
		return new GoogleOpenIdClient();
	}
	
	@Bean
	public MyOpenIdClient myOpenIdClient() {
		return new MyOpenIdClient();
	}

	@Bean
	public Clients clients() {
		return new Clients(configurer.getAuthenticationCallbackBaseUrl() + Pac4jAuthenticationUtils.CALLBACK_URI,
				myOpenIdClient(), googleClient(), gitHubClient(), twitterClient());
	}
	
	@Bean
	public ServletContextAttributeExporter servletContextAttributeExporter() {
		ServletContextAttributeExporter exporter = new ServletContextAttributeExporter();
		exporter.setAttributes(
				ImmutableMap.<String, Object>builder()
				.put(Pac4jClient.MYOPENID.getClientKey(), myOpenIdClient())
				.put(Pac4jClient.GOOGLE.getClientKey(), googleClient())
				.put(Pac4jClient.GITHUB.getClientKey(), gitHubClient())
				.put(Pac4jClient.TWITTER.getClientKey(), twitterClient())
				.build()
		);
		return exporter;
	}
	
	@Autowired
	@Bean
	public ClientAuthenticationFilter clientAuthenticationFilter(AuthenticationManager authenticationManager, Clients clients) {
		ClientAuthenticationFilter filter = new ClientAuthenticationFilter("/" + Pac4jAuthenticationUtils.CALLBACK_URI);
		filter.setClients(clients);
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationFailureHandler(pac4jAuthenticationFailureHandler());
		filter.setAuthenticationSuccessHandler(pac4jAuthenticationSuccessHandler());
		return filter;
	}
	
	@Autowired
	@Bean
	public ClientAuthenticationProvider clientAuthenticationProvider(Clients clients) {
		ClientAuthenticationProvider provider = new ClientAuthenticationProvider();
		provider.setClients(clients);
		return provider;
	}
	
	@Bean
	public Pac4jUserDetailsService pac4jUserDetailsService() {
		return new Pac4jUserDetailsService();
	}
	
	@Bean
	public SimpleUrlAuthenticationFailureHandler pac4jAuthenticationFailureHandler() {
		return new Pac4jAuthenticationFailureHandler();
	}
	
	@Bean
	public SimpleUrlAuthenticationSuccessHandler pac4jAuthenticationSuccessHandler() {
		return new Pac4jAuthenticationSuccessHandler();
	}
	
	@Bean(name = "authenticationService")
	public IAuthenticationService authenticationService() {
		return new Pac4jAuthenticationServiceImpl();
	}
	
	@PostConstruct
	public void addPac4jToAuthenticationManager() {
		authenticationManager.getProviders().add(clientAuthenticationProvider(clients()));
	}
}
