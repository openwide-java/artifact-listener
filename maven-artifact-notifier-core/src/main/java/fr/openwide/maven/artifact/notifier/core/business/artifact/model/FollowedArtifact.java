package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.bindgen.Bindable;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Indexed
@Bindable
@Cacheable
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "artifact_id", "user_id" }) })
public class FollowedArtifact extends GenericEntity<Long, FollowedArtifact> {

	private static final long serialVersionUID = 3705746974199339801L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Index(name = "idx_Artifact_followed_id")
	private Artifact artifact;
	
	@Column
	private Date lastNotifiedVersionDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Index(name = "idx_User_id")
	private User user;
	
	@OneToMany(mappedBy = "followedArtifact", cascade = CascadeType.ALL)
	private List<ArtifactNotificationRule> artifactNotificationRules = Lists.newArrayList();
	
	@Column(nullable = false)
	private Date creationDate = new Date();
	
	public FollowedArtifact() {
	}
	
	public FollowedArtifact(Artifact artifact) {
		this.artifact = artifact;
		if (artifact.getMostRecentVersion() != null) {
			this.lastNotifiedVersionDate = artifact.getMostRecentVersion().getLastUpdateDate();
		}
	}
	
	public FollowedArtifact copyforUser(User user) {
		FollowedArtifact target = new FollowedArtifact();
		target.setUser(user);
		target.setArtifact(artifact);
		target.setCreationDate(creationDate);
		target.setLastNotifiedVersionDate(lastNotifiedVersionDate);
		for (ArtifactNotificationRule rule : artifactNotificationRules) {
			target.addArtifactNotificationRule(rule.copyForFollowedArtifact(target));
		}
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

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public Date getLastNotifiedVersionDate() {
		return CloneUtils.clone(lastNotifiedVersionDate);
	}

	public void setLastNotifiedVersionDate(Date lastNotifiedVersionDate) {
		this.lastNotifiedVersionDate = CloneUtils.clone(lastNotifiedVersionDate);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<ArtifactNotificationRule> getArtifactNotificationRules() {
		return artifactNotificationRules;
	}
	
	public void addArtifactNotificationRule(ArtifactNotificationRule notificationRule) {
		if (!artifactNotificationRules.contains(notificationRule)) {
			artifactNotificationRules.add(notificationRule);
			notificationRule.setFollowedArtifact(this);
		}
	}
	
	public Date getCreationDate() {
		return CloneUtils.clone(creationDate);
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = CloneUtils.clone(creationDate);
	}

	@Override
	@JsonIgnore
	public String getNameForToString() {
		return getDisplayName();
	}

	@Override
	@JsonIgnore
	public String getDisplayName() {
		if (artifact != null) {
			return artifact.getNameForToString();
		}
		return "";
	}

	@Override
	public int compareTo(FollowedArtifact other) {
		if (this.equals(other)) {
			return 0;
		}
		return this.getArtifact().compareTo(other.getArtifact());
	}
	
}
