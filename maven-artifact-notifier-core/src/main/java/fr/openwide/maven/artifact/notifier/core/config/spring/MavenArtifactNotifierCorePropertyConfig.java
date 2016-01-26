package fr.openwide.maven.artifact.notifier.core.config.spring;

import org.springframework.context.annotation.Configuration;

import fr.openwide.core.jpa.more.business.parameter.dao.ParameterDaoImpl;
import fr.openwide.core.spring.config.spring.AbstractApplicationPropertyConfig;
import fr.openwide.core.spring.property.dao.IMutablePropertyDao;
import fr.openwide.core.spring.property.service.IPropertyRegistry;
import fr.openwide.maven.artifact.notifier.core.property.MavenArtifactNotifierCorePropertyIds;

@Configuration
public class MavenArtifactNotifierCorePropertyConfig extends AbstractApplicationPropertyConfig {

	@Override
	protected void register(IPropertyRegistry registry) {
		registry.registerDate(MavenArtifactNotifierCorePropertyIds.LAST_SYNCHRONIZATION_DATE);
	}

	@Override
	public IMutablePropertyDao mutablePropertyDao() {
		return new ParameterDaoImpl();
	}

}
