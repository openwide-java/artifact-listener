package fr.openwide.maven.artifact.notifier.core.business.notification.service.fluid;

import java.util.List;
import java.util.Locale;

import fr.openwide.core.spring.notification.model.INotificationRecipient;


public interface INotificationBuilderBuildState {

	INotificationBuilderBuildState cc(String... cc);
	
	INotificationBuilderBuildState cc(INotificationRecipient cc);
	
	INotificationBuilderBuildState cc(List<? extends INotificationRecipient> cc);

	INotificationBuilderBuildState bcc(String... bcc);
	
	INotificationBuilderBuildState bcc(INotificationRecipient bcc);
	
	INotificationBuilderBuildState bcc(List<? extends INotificationRecipient> bcc);
	
	INotificationBuilderBodyState subject(String subject);
	
	INotificationBuilderBodyState subject(String prefix, String subject);
	
	INotificationBuilderBuildState variable(String name, Object value);
	
	INotificationBuilderBuildState variable(String name, Object value, Locale locale);
	
	INotificationBuilderSendState template(String templateKey);
}
