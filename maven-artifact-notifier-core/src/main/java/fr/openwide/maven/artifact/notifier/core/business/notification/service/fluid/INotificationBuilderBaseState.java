package fr.openwide.maven.artifact.notifier.core.business.notification.service.fluid;

import java.util.List;

import fr.openwide.core.spring.notification.model.INotificationRecipient;

public interface INotificationBuilderBaseState {
	
	INotificationBuilderBaseState from(String from);
	
	INotificationBuilderBuildState to(String... to);
	
	INotificationBuilderBuildState to(INotificationRecipient to);

	INotificationBuilderBuildState to(List<? extends INotificationRecipient> to);
}
