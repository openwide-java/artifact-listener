package fr.openwide.maven.artifact.notifier.core.business.notification.service.fluid;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.util.MultiValueMap;

import freemarker.template.TemplateException;

public interface INotificationBuilderSendState {
	
	INotificationBuilderSendState htmlBody(String htmlBody);
	
	INotificationBuilderSendState htmlBody(String htmlBody, Locale locale);

	INotificationBuilderSendState attach(String attachmentFilename, File file);
	
	INotificationBuilderSendState attach(Map<String, File> attachments);

	INotificationBuilderSendState inline(String contentId, File file);

	INotificationBuilderSendState header(String name, String value);
	
	INotificationBuilderSendState headers(MultiValueMap<String, String> headers);
	
	INotificationBuilderSendState priority(int priority);
	
	void send() throws MessagingException, IOException, TemplateException;
	
	void send(String encoding) throws MessagingException, IOException, TemplateException;
}
