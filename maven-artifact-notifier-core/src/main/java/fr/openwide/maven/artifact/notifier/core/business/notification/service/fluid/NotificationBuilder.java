package fr.openwide.maven.artifact.notifier.core.business.notification.service.fluid;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.exec.util.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.openwide.core.spring.config.CoreConfigurer;
import fr.openwide.core.spring.notification.model.INotificationRecipient;
import fr.openwide.core.spring.util.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class NotificationBuilder implements INotificationBuilderBaseState, INotificationBuilderBuildState,
		INotificationBuilderBodyState, INotificationBuilderSendState {

	private static final String DEFAULT_MAIL_ENCODING = "utf-8";
	
	private static final String NEW_LINE_TEXT_PLAIN = StringUtils.NEW_LINE_ANTISLASH_N;
	private static final String NEW_LINE_HTML = "<br />";
	
	private static final String DEV_SUBJECT_PREFIX = "[Dev]";
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private CoreConfigurer configurer;
	
	@Autowired
	@Qualifier(value = "freemarkerMailConfiguration")
	private Configuration templateConfiguration;
	
	private String from;
	
	private MultiValueMap<Locale, String> emailsByLocale = new LinkedMultiValueMap<Locale, String>();
	
	private Set<String> cc = Sets.newLinkedHashSet();
	
	private Set<String> bcc = Sets.newLinkedHashSet();
	
	private String templateKey;
	
	private Map<Locale, HashMap<String, Object>> templateVariablesByLocale = new HashMap<Locale, HashMap<String,Object>>();
	
	private String subject;
	
	private String textBody;
	
	private Map<Locale, String> htmlBodyByLocale = Maps.newHashMap();

	private Map<String, File> attachments = Maps.newHashMap();
	
	private Map<String, File> inlines = Maps.newHashMap();
	
	private MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
	
	private int priority;
	
	public NotificationBuilder() {
	}
	
	@Override
	public INotificationBuilderBaseState from(String from) {
		Assert.hasText(from, "Sender's email address must contain text");
		this.from = from;
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState to(String... to) {
		if (!ObjectUtils.isEmpty(to)) {
			for (String email : to) {
				if (StringUtils.hasText(email)) {
					emailsByLocale.add(getDefaultLocale(), email);
				}
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState to(INotificationRecipient to) {
		if (to != null && StringUtils.hasText(to.getEmail())) {
			emailsByLocale.add(getLocale(to), to.getEmail());
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState to(List<? extends INotificationRecipient> to) {
		if (to != null) {
			for (INotificationRecipient receiver : to) {
				to(receiver);
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState cc(String... cc) {
		if (!ObjectUtils.isEmpty(cc)) {
			for (String email : cc) {
				if (StringUtils.hasText(email)) {
					this.cc.add(email);
				}
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState cc(INotificationRecipient cc) {
		if (cc != null && StringUtils.hasText(cc.getEmail())) {
			this.cc.add(cc.getEmail());
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState cc(List<? extends INotificationRecipient> cc) {
		if (cc != null) {
			for (INotificationRecipient receiver : cc) {
				cc(receiver);
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState bcc(String... bcc) {
		if (!ObjectUtils.isEmpty(bcc)) {
			for (String email : bcc) {
				if (StringUtils.hasText(email)) {
					this.bcc.add(email);
				}
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState bcc(INotificationRecipient bcc) {
		if (bcc != null && StringUtils.hasText(bcc.getEmail())) {
			this.bcc.add(bcc.getEmail());
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState bcc(List<? extends INotificationRecipient> bcc) {
		if (bcc != null) {
			for (INotificationRecipient receiver : bcc) {
				bcc(receiver);
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState variable(String name, Object value) {
		return variable(name, value, null);
	}
	
	// If locale == null, the variable will be considered as not locale-sensitive and will be available for all locales
	@Override
	public INotificationBuilderBuildState variable(String name, Object value, Locale locale) {
		Assert.hasText(name, "Variable name must contain text");
		if (!templateVariablesByLocale.containsKey(locale)) {
			templateVariablesByLocale.put(locale, new HashMap<String, Object>());
		}
		templateVariablesByLocale.get(locale).put(name, value);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState template(String templateKey) {
		Assert.hasText(templateKey, "Template key must contain text");
		this.templateKey = templateKey;
		return this;
	}
	
	@Override
	public INotificationBuilderBodyState subject(String subject) {
		return subject(getSubjectPrefix(), subject);
	}
	
	@Override
	public INotificationBuilderBodyState subject(String prefix, String subject) {
		Assert.hasText(subject, "Email subject must contain text");
		if (StringUtils.hasText(prefix)) {
			this.subject = prefix + " " + subject;
		} else {
			this.subject = subject;
		}
		return this;
	}
	
	@Override
	public INotificationBuilderSendState textBody(String textBody) {
		this.textBody = textBody;
		return this;
	}
	
	@Override
	public INotificationBuilderSendState htmlBody(String htmlBody) {
		return htmlBody(htmlBody, null);
	}
	
	// If locale == null, the associated htmlBody will be used for any locale that do not have one
	@Override
	public INotificationBuilderSendState htmlBody(String htmlBody, Locale locale) {
		this.htmlBodyByLocale.put(locale, htmlBody);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState attach(String attachmentFilename, File file) {
		Assert.hasText(attachmentFilename, "Attachment filename must contain text");
		Assert.notNull(file, "Attached file must not be null");
		this.attachments.put(attachmentFilename, file);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState attach(Map<String, File> attachments) {
		Assert.notNull(attachments, "Attachment map must not be null");
		for (Map.Entry<String, File> attachment : attachments.entrySet()) {
			attach(attachment.getKey(), attachment.getValue());
		}
		return this;
	}
	
	@Override
	public INotificationBuilderSendState inline(String contentId, File file) {
		Assert.hasText(contentId, "Content ID must contain text");
		Assert.notNull(file, "Inline file must not be null");
		this.inlines.put(contentId, file);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState header(String name, String value) {
		Assert.hasText(name, "Header name must contain text");
		Assert.hasText(value, "Header value must contain text");
		this.headers.add(name, value);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState headers(MultiValueMap<String, String> headers) {
		Assert.notNull(headers, "Header map must not be null");
		for (Entry<String, List<String>> header : headers.entrySet()) {
			for (String value : header.getValue()) {
				header(header.getKey(), value);
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderSendState priority(int priority) {
		if (priority < 1 || priority > 5) {
			throw new IllegalArgumentException("Priority must be between 1 (highest) and 5 (lowest)");
		}
		this.priority = priority;
		return this;
	}
	
	@Override
	public void send() throws MessagingException, IOException, TemplateException {
		send(DEFAULT_MAIL_ENCODING);
	}

	@Override
	public void send(String encoding) throws MessagingException, IOException, TemplateException {
		for (Entry<Locale, List<String>> entry : emailsByLocale.entrySet()) {
			String[] to = entry.getValue().toArray(new String[entry.getValue().size()]);
			MimeMessage message = buildMessage(to, encoding, entry.getKey());
			
			if (message == null) {
				continue;
			}
			mailSender.send(message);
		}
	}
	
	private MimeMessage buildMessage(String[] to, String encoding, Locale locale) throws IOException, TemplateException, MessagingException {
		if (templateKey != null) {
			HashMap<String, Object> templateVariables = getTemplateVariables(locale);
			subject(getSubject(templateKey, templateVariables, locale));
			textBody = getBodyText(templateKey, templateVariables, locale);
		}
		String htmlBody = getHtmlBody(locale);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, isMultipartNeeded(htmlBody), encoding);
		
		if (from == null) {
			from = getFrom();
		}
		String[] filteredTo = filterEmails(to);
		if (filteredTo.length == 0) {
			return null;
		}
		
		helper.setFrom(from);
		helper.setTo(filteredTo);
		helper.setCc(cc.toArray(new String[cc.size()]));
		helper.setBcc(bcc.toArray(new String[bcc.size()]));
		helper.setSubject(subject);

		String textBodyPrefix = getBodyPrefix(to, false);
		String htmlBodyPrefix = getBodyPrefix(to, true);
		if (StringUtils.hasText(textBody) && StringUtils.hasText(htmlBody)) {
			helper.setText(textBodyPrefix + textBody, htmlBodyPrefix + htmlBody);
		} else if (StringUtils.hasText(htmlBody)) {
			helper.setText(htmlBodyPrefix + htmlBody, true);
		} else {
			helper.setText(textBodyPrefix + textBody);
		}
	
		for (Map.Entry<String, File> attachment : attachments.entrySet()) {
			helper.addAttachment(attachment.getKey(), attachment.getValue());
		}
		for (Map.Entry<String, File> inline : inlines.entrySet()) {
			helper.addInline(inline.getKey(), inline.getValue());
		}
		for (Entry<String, List<String>> header : headers.entrySet()) {
			for (String value : header.getValue()) {
				message.addHeader(header.getKey(), value);
			}
		}
		if (priority != 0) {
			helper.setPriority(priority);
		}
		return message;
	}
	
	private boolean isMultipartNeeded(String htmlBody) {
		boolean alternativeTexts = StringUtils.hasText(textBody) && StringUtils.hasText(htmlBody);
		return alternativeTexts || !attachments.isEmpty() || !inlines.isEmpty();
	}
	
	private String getHtmlBody(Locale locale) {
		String htmlBody = htmlBodyByLocale.get(locale);
		if (htmlBody == null) {
			htmlBody = htmlBodyByLocale.get(null);
		}
		return htmlBody;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getTemplateVariables(Locale locale) {
		HashMap<String, Object> sharedVariables = templateVariablesByLocale.get(null);
		HashMap<String, Object> localeSpecificVariables = templateVariablesByLocale.get(locale);
		return (HashMap<String, Object>) MapUtils.merge(sharedVariables, localeSpecificVariables);
	}
	
	private String getFrom() {
		return configurer.getNotificationMailFrom();
	}
	
	private Locale getDefaultLocale() {
		return configurer.getDefaultLocale();
	}
	
	private Locale getLocale(INotificationRecipient recipient) {
		return configurer.toAvailableLocale(recipient.getLocale());
	}
	
	private String[] filterEmails(String[] emails) {
		if (configurer.isConfigurationTypeDevelopment()) {
			return configurer.getNotificationTestEmails();
		}
		return emails;
	}
	
	private String getSubjectPrefix() {
		StringBuilder subjectPrefix = new StringBuilder();
		subjectPrefix.append(configurer.getNotificationMailSubjectPrefix());
		if (configurer.isConfigurationTypeDevelopment()) {
			subjectPrefix.append(DEV_SUBJECT_PREFIX);
		}
		return subjectPrefix.toString();
	}
	
	private String getBodyPrefix(String[] to, boolean isHtml) {
		if (configurer.isConfigurationTypeDevelopment()) {
			
			String newLine = isHtml ? NEW_LINE_HTML : NEW_LINE_TEXT_PLAIN;
			
			StringBuffer newBody = new StringBuffer();
			newBody.append("#############").append(newLine);
			newBody.append("#").append(newLine);
			newBody.append("# To: ").append(StringUtils.join(to, ", ")).append(newLine);
			newBody.append("#").append(newLine);
			newBody.append("#############").append(newLine).append(newLine).append(newLine);
			
			return newBody.toString();
		}
		return "";
	}
	
	private String getBodyText(String key, HashMap<String, Object> map, Locale locale)
			throws IOException, TemplateException {
		return getMailElement(key, MailElement.BODY_TEXT, map, locale);
	}
	
	private String getSubject(String key, HashMap<String, Object> map, Locale locale)
			throws IOException, TemplateException {
		return getMailElement(key, MailElement.SUBJECT, map, locale);
	}
	
	@SuppressWarnings("unchecked")
	private String getMailElement(String key, MailElement element, HashMap<String, Object> map, Locale locale)
			throws IOException, TemplateException {
		if (map.containsKey(element.toString())) {
			throw new IllegalStateException(String.format("Provided map must not contain %1$s key", element));
		}
		Map<String, Object> fMap = (Map<String, Object>) map.clone();
		fMap.put(element.toString(), true);
		return FreeMarkerTemplateUtils.processTemplateIntoString(templateConfiguration.getTemplate(key, locale), fMap);
	}
	
	private enum MailElement {
		BODY_TEXT,
		SUBJECT
	}
}
