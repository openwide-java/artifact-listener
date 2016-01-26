package fr.openwide.maven.artifact.notifier.core.config.spring;

import java.util.Collection;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.openwide.core.jpa.more.business.task.model.IQueueId;
import fr.openwide.core.jpa.more.config.spring.AbstractTaskManagementConfig;
import fr.openwide.maven.artifact.notifier.core.business.task.model.MavenArtifactNotifierTaskQueueId;

@Configuration
public class MavenArtifactNotifierCoreTaskManagementConfig extends AbstractTaskManagementConfig {

	@Override
	@Bean
	public Collection<? extends IQueueId> queueIds() {
		return EnumUtils.getEnumList(MavenArtifactNotifierTaskQueueId.class);
	}

}
