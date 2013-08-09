package fr.openwide.maven.artifact.notifier.core.business.project.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.bindgen.Bindable;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.IComparableVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.util.MavenCentralVersionComparator;

@Indexed
@Bindable
@Cacheable
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "project_id", "version" }) })
public class ProjectVersion extends GenericEntity<Long, ProjectVersion> implements IComparableVersion {
	
	private static final long serialVersionUID = -2763422157287695696L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Project project;
	
	@Column(nullable = false)
	private String version;
	
	@Embedded
	private VersionAdditionalInformation additionalInformation = new VersionAdditionalInformation();
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ProjectVersionStatus status;
	
	@Column(nullable = false)
	private Date creationDate;
	
	@Column(nullable = false)
	private Date lastUpdateDate;
	
	protected ProjectVersion() {
	}
	
	public ProjectVersion(String version) {
		this.version = version;
		this.lastUpdateDate = new Date();
		this.creationDate = new Date();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public VersionAdditionalInformation getAdditionalInformation() {
		if (additionalInformation == null) {
			additionalInformation = new VersionAdditionalInformation();
		}
		return additionalInformation;
	}

	public void setAdditionalInformation(VersionAdditionalInformation additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
	
	public ProjectVersionStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectVersionStatus status) {
		this.status = status;
	}

	@Override
	public Date getLastUpdateDate() {
		return CloneUtils.clone(lastUpdateDate);
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = CloneUtils.clone(lastUpdateDate);
	}
	
	public Date getCreationDate() {
		return CloneUtils.clone(creationDate);
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = CloneUtils.clone(creationDate);
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getNameForToString() {
		return version;
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return version;
	}
	
	@Override
	public int compareTo(ProjectVersion other) {
		if (this.equals(other)) {
			return 0;
		}
		return MavenCentralVersionComparator.reverse().compare(this, other);
	}
}
