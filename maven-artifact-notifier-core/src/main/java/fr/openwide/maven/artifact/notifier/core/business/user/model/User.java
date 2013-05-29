package fr.openwide.maven.artifact.notifier.core.business.user.model;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

import org.bindgen.Bindable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;
import fr.openwide.core.jpa.security.business.person.model.AbstractPerson;
import fr.openwide.core.jpa.security.business.person.model.IPersonGroup;
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
public class User extends AbstractPerson<User> {
	
	private static final long serialVersionUID = 1508647513049577617L;
	
	public static final String FULL_NAME_SORT_FIELD_NAME = "fullNameSort";
	
	public static final int MIN_USERNAME_LENGTH = 3;
	public static final int MAX_USERNAME_LENGTH = 25;
	
	public static final int MIN_PASSWORD_LENGTH = 6;
	public static final int MAX_PASSWORD_LENGTH = 15;
	
	@Column(unique = true)
	private String openIdIdentifier;
	
	@Column
	@Fields({
		@Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT)),
		@Field(name = FULL_NAME_SORT_FIELD_NAME, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT))
	})
	private String fullName;
	
	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinTable(uniqueConstraints = { @UniqueConstraint(columnNames = { "persons_id", "usergroups_id" }) })
	private List<UserGroup> userGroups = Lists.newArrayList();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private List<FollowedArtifact> followedArtifacts = Lists.newArrayList();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private List<ArtifactVersionNotification> notifications = Lists.newArrayList();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private List<EmailAddress> additionalEmails = Lists.newArrayList();
	
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	@Column(unique = true)
	private String notificationHash;
	
	@Column(nullable = false)
	private boolean notificationAllowed = true;
	
	public User() {
		super();
	}
	
	public String getOpenIdIdentifier() {
		return openIdIdentifier;
	}
	
	public void setOpenIdIdentifier(String openIdIdentifier) {
		this.openIdIdentifier = openIdIdentifier;
	}
	
	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
	
	public List<FollowedArtifact> getFollowedArtifacts() {
		return followedArtifacts;
	}
	
	public void addFollowedArtifact(FollowedArtifact followedArtifact) {
		if (!followedArtifacts.contains(followedArtifact)) {
			followedArtifacts.add(followedArtifact);
			followedArtifact.setUser(this);
		}
	}

	public List<ArtifactVersionNotification> getNotifications() {
		return notifications;
	}

	public void addNotification(ArtifactVersionNotification notification) {
		if (!notifications.contains(notification)) {
			notifications.add(notification);
			notification.setUser(this);
		}
	}
	
	public List<EmailAddress> getAdditionalEmails() {
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IPersonGroup> getPersonGroups() {
		return (List<IPersonGroup>) (Object) getUserGroups();
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
