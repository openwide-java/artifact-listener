package fr.openwide.maven.artifact.notifier.web.application.config.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import fr.openwide.maven.artifact.notifier.web.application.auth.model.NormalizedOpenIdAttributesBuilder;
import fr.openwide.maven.artifact.notifier.web.application.auth.service.OpenIdAuthenticationFailureHandler;
import fr.openwide.maven.artifact.notifier.web.application.auth.service.OpenIdUserDetailsService;

@Configuration
@ImportResource({ "classpath:spring/security-web-context.xml" })
public class MavenArtifactNotifierWebappSecurityConfig {
	
	@Bean(name="openIdUserService")
	public UserDetailsService openIdUserDetailsService() {
		return new OpenIdUserDetailsService();
	}
	
	@Autowired
	@Bean(name="openIdAuthenticationFailureHandler")
	public SimpleUrlAuthenticationFailureHandler openIDAuthenticationFailureHandler(NormalizedOpenIdAttributesBuilder normalizedOpenIdAttributesBuilder) {
		return new OpenIdAuthenticationFailureHandler(normalizedOpenIdAttributesBuilder);
	}
}
