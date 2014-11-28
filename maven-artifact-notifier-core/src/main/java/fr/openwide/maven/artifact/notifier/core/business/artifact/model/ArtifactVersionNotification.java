package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.time.DateUtils;
import org.bindgen.Bindable;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Indexed
@Bindable
@Cacheable
@Entity
public class ArtifactVersionNotification extends GenericEntity<Long, ArtifactVersionNotification> {

	private static final long serialVersionUID = 1876648775290106906L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Index(name = "idx_Artifact_version_id")
	private ArtifactVersion artifactVersion;
	
	@Column(nullable = false)
	private Date creationDate = new Date();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Index(name = "idx_User_to_notify_id")
	private User user;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@Index(name = "idx_ArtifactVersionNotification_status")
	private ArtifactVersionNotificationStatus status = ArtifactVersionNotificationStatus.PENDING;
	
	public ArtifactVersionNotification() {
	}
	
	public ArtifactVersionNotification(ArtifactVersion artifactVersion) {
		this.artifactVersion = artifactVersion;
	}
	
	@Override
	public Long getId() {
 		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public ArtifactVersion getArtifactVersion() {
		return artifactVersion;
	}

	public void setArtifactVersion(ArtifactVersion artifactVersion) {
		this.artifactVersion = artifactVersion;
	}

	public Date getCreationDate() {
		return CloneUtils.clone(creationDate);
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = CloneUtils.clone(creationDate);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public ArtifactVersionNotificationStatus getStatus() {
		return status;
	}

	public void setStatus(ArtifactVersionNotificationStatus status) {
		this.status = status;
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getNameForToString() {
		return artifactVersion == null ? null : artifactVersion.getNameForToString();
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return toString();
	}

	@Override
	public int compareTo(ArtifactVersionNotification other) {
		if (this.equals(other)) {
			return 0;
		}
		int result;
		if (DateUtils.isSameDay(this.getCreationDate(), other.getCreationDate())) {
			result = this.getArtifactVersion().getArtifact().compareTo(other.getArtifactVersion().getArtifact());
			if (result == 0) {
				result = this.getArtifactVersion().compareTo(other.getArtifactVersion());
			}
		} else {
			result = (this.getCreationDate().before(other.getCreationDate()) ? -1 : 1);
		}
		return result;
	}
}
