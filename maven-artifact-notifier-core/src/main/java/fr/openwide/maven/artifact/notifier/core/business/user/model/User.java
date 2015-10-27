package fr.openwide.maven.artifact.notifier.core.business.user.model;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import org.bindgen.Bindable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;
import fr.openwide.core.jpa.security.business.person.model.GenericSimpleUser;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;

@Indexed
@Bindable
@Cacheable
@Entity(name = "user_")
@AttributeOverrides({
		@AttributeOverride(name = "firstName", column = @Column(nullable = true)),
		@AttributeOverride(name = "lastName", column = @Column(nullable = true))
})
public class User extends GenericSimpleUser<User, UserGroup> {
	
	private static final long serialVersionUID = 1508647513049577617L;
	
	public static final String FULL_NAME_SORT_FIELD_NAME = "fullNameSort";
	
	public static final int MIN_USERNAME_LENGTH = 3;
	public static final int MAX_USERNAME_LENGTH = 25;
	
	public static final int MIN_PASSWORD_LENGTH = 6;
	public static final int MAX_PASSWORD_LENGTH = 15;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthenticationType authenticationType = AuthenticationType.LOCAL;
	
	@Column(unique = true)
	private String remoteIdentifier;
	
	@Column
	@Fields({
		@Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT)),
		@Field(name = FULL_NAME_SORT_FIELD_NAME, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT))
	})
	private String fullName;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@SortNatural
	private Set<FollowedArtifact> followedArtifacts = Sets.newTreeSet();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@SortNatural
	private Set<ArtifactVersionNotification> notifications = Sets.newTreeSet();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@SortNatural
	private Set<EmailAddress> additionalEmails = Sets.newTreeSet();
	
	@JsonIgnore
	@Column(unique = true)
	private String notificationHash;
	
	@Column(nullable = false)
	private boolean notificationAllowed = true;
	
	public User() {
	}
	
	public void copyProfileToUser(User target) {
		for (FollowedArtifact followedArtifact : followedArtifacts) {
			target.addFollowedArtifact(followedArtifact.copyforUser(target));
		}
		for (ArtifactVersionNotification notification : notifications) {
			target.addNotification(notification.copyForUser(target));
		}
		for (EmailAddress emailAddress : additionalEmails) {
			target.addAdditionalEmail(emailAddress.copyForUser(target));
		}
	}
	
	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}
	
	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}
	
	public String getRemoteIdentifier() {
		return remoteIdentifier;
	}
	
	public void setRemoteIdentifier(String remoteIdentifier) {
		this.remoteIdentifier = remoteIdentifier;
	}
	
	public Set<FollowedArtifact> getFollowedArtifacts() {
		return followedArtifacts;
	}
	
	public void addFollowedArtifact(FollowedArtifact followedArtifact) {
		if (!followedArtifacts.contains(followedArtifact)) {
			followedArtifacts.add(followedArtifact);
			followedArtifact.setUser(this);
		}
	}

	public Set<ArtifactVersionNotification> getNotifications() {
		return notifications;
	}

	public void addNotification(ArtifactVersionNotification notification) {
		if (!notifications.contains(notification)) {
			notifications.add(notification);
			notification.setUser(this);
		}
	}
	
	public Set<EmailAddress> getAdditionalEmails() {
		return additionalEmails;
	}

	public void addAdditionalEmail(EmailAddress emailAddress) {
		if (!additionalEmails.contains(emailAddress)) {
			additionalEmails.add(emailAddress);
			emailAddress.setUser(this);
		}
	}
	
	public String getNotificationHash() {
		return notificationHash;
	}
	
	public void setNotificationHash(String notificationHash) {
		this.notificationHash = notificationHash;
	}
	
	public boolean isNotificationAllowed() {
		return notificationAllowed;
	}
	
	public void setNotificationAllowed(boolean notificationAllowed) {
		this.notificationAllowed = notificationAllowed;
	}
	
	@Override
	public String getDisplayName() {
		if (StringUtils.hasText(fullName)) {
			return fullName;
		} else {
			return getUserName();
		}
	}
	
	@Override
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@Override
	public int compareTo(User user) {
		if (this.equals(user)) {
			return 0;
		}
		
		return DEFAULT_STRING_COLLATOR.compare(this.getUserName(), user.getUserName());
	}
	
}
