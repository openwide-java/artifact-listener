package fr.openwide.maven.artifact.notifier.core.config.spring;

import org.springframework.context.annotation.Configuration;

import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;

@Configuration
@ConfigurationLocations(locations = "classpath:owsi-core-0.11-postgresql-workaround.properties", order = 1000)
public class Postgresql96WorkaroundConfig {

}
