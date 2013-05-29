package fr.openwide.maven.artifact.notifier.core.config.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class MavenArtifactNotifierCoreNotificationConfig {

	@Bean
	public JavaMailSenderImpl mailSender(@Value("${notification.smtp.host}") String host,
			@Value("${notification.smtp.port}") int port) {
		JavaMailSenderImpl javaMailSend = new JavaMailSenderImpl();
		javaMailSend.setHost(host);
		javaMailSend.setPort(port);
		return javaMailSend;
	}

	@Bean
	public FreeMarkerConfigurationFactoryBean freemarkerMailConfiguration() {
		FreeMarkerConfigurationFactoryBean configuration = new FreeMarkerConfigurationFactoryBean();
		configuration.setTemplateLoaderPath("classpath:notification");
		return configuration;
	}

}
