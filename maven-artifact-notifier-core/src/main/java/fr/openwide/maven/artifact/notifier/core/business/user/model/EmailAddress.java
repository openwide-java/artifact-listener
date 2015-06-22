package fr.openwide.maven.artifact.notifier.core.business.user.model;

import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bindgen.Bindable;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.spring.notification.model.INotificationRecipient;

@Indexed
@Bindable
@Cacheable
@Entity
public class EmailAddress extends GenericEntity<Long, EmailAddress> implements INotificationRecipient {

	private static final long serialVersionUID = 8672835888543112706L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@Column(nullable = false)
	private String email;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Index(name = "idx_email_User_id")
	private User user;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private EmailStatus status = EmailStatus.PENDING_CONFIRM;
	
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	@Column(unique = true)
	private String emailHash;
	
	public EmailAddress() {
	}
	
	public EmailAddress(String email) {
		this.email = email;
	}
	
	public EmailAddress copyForUser(User user) {
		EmailAddress target = new EmailAddress();
		target.setEmail(email);
		target.setStatus(status);
		target.setUser(user);

		StringBuilder sb = new StringBuilder();
		sb.append(RandomStringUtils.randomAscii(8))
				.append(user.getId())
				.append(email)
				.append(user.getCreationDate());
		target.setEmailHash(DigestUtils.sha1Hex(sb.toString()));

		return target;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public EmailStatus getStatus() {
		return status;
	}
	
	public void setStatus(EmailStatus status) {
		this.status = status;
	}
	
	public String getEmailHash() {
		return emailHash;
	}
	
	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}
	
	@Override
	public Locale getLocale() {
		if (user != null) {
			return user.getLocale();
		}
		return null;
	}

	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	@Transient
	@Override
	public String getFullName() {
		return getDisplayName();
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getNameForToString() {
		return getDisplayName();
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return email;
	}
	
	@Override
	public int compareTo(EmailAddress other) {
		if (this.equals(other)) {
			return 0;
		}
		return this.email.compareTo(other.getEmail());
	}

	@Override
	public boolean isNotificationEnabled() {
		return true;
	}
}
