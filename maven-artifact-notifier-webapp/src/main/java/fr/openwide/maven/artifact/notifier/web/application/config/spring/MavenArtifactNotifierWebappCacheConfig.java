package fr.openwide.maven.artifact.notifier.web.application.config.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:spring/cache-web-context.xml" })
public class MavenArtifactNotifierWebappCacheConfig {

}
