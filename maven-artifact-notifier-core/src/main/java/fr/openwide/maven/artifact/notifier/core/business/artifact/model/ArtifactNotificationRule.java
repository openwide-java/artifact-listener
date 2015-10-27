package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.bindgen.Bindable;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;

@Indexed
@Bindable
@Cacheable
@Entity
public class ArtifactNotificationRule extends GenericEntity<Long, ArtifactNotificationRule> {

	private static final long serialVersionUID = 512871067241380923L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Index(name = "idx_Followed_Artifact_id")
	private FollowedArtifact followedArtifact;
	
	@Column(nullable = false)
	private String regex;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ArtifactNotificationRuleType type = ArtifactNotificationRuleType.COMPLY;
	
	public ArtifactNotificationRule() {
	}
	
	public ArtifactNotificationRule(FollowedArtifact followedArtifact, String regex, ArtifactNotificationRuleType type) {
		this.followedArtifact = followedArtifact;
		this.regex = regex;
		this.type = type;
	}
	
	public ArtifactNotificationRule copyForFollowedArtifact(FollowedArtifact followedArtifact) {
		ArtifactNotificationRule target = new ArtifactNotificationRule();
		target.setFollowedArtifact(followedArtifact);
		target.setRegex(regex);
		target.setType(type);
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

	public FollowedArtifact getFollowedArtifact() {
		return followedArtifact;
	}
	
	public void setFollowedArtifact(FollowedArtifact followedArtifact) {
		this.followedArtifact = followedArtifact;
	}
	
	public String getRegex() {
		return regex;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public ArtifactNotificationRuleType getType() {
		return type;
	}
	
	public void setType(ArtifactNotificationRuleType type) {
		this.type = type;
	}

	@Override
	@JsonIgnore
	public String getNameForToString() {
		return getDisplayName();
	}

	@Override
	@JsonIgnore
	public String getDisplayName() {
		return regex;
	}

	@Override
	public int compareTo(ArtifactNotificationRule other) {
		if (this.equals(other)) {
			return 0;
		}
		if (this.getType().equals(other.getType())) {
			return this.getRegex().compareTo(other.getRegex());
		} else if (this.getType().equals(ArtifactNotificationRuleType.COMPLY)) {
			return -1;
		}
		return 1;
	}
}
