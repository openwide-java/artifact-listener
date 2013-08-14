package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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
import fr.openwide.maven.artifact.notifier.core.business.artifact.util.MavenCentralVersionComparator;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.VersionAdditionalInformation;

@Indexed
@Bindable
@Cacheable
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "artifact_id", "version" }) })
public class ArtifactVersion extends GenericEntity<Long, ArtifactVersion> implements IComparableVersion {

	private static final long serialVersionUID = -5029816694989808672L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Artifact artifact;
	
	@Column(nullable = false)
	private String version;
	
	@Embedded
	private VersionAdditionalInformation additionalInformation = new VersionAdditionalInformation();
	
	@ManyToOne(fetch = FetchType.LAZY)
	private ProjectVersion projectVersion;
	
	@Column(nullable = false)
	private Date lastUpdateDate;
	
	@Column
	private Date importDate;
	
	protected ArtifactVersion() {
	}
	
	public ArtifactVersion(String version, Date lastUpdateDate) {
		this.version = version;
		this.lastUpdateDate = CloneUtils.clone(lastUpdateDate);
		this.importDate = new Date();
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
	
	public ProjectVersion getProjectVersion() {
		return projectVersion;
	}
	
	public void setProjectVersion(ProjectVersion projectVersion) {
		this.projectVersion = projectVersion;
	}

	@Override
	public Date getLastUpdateDate() {
		return CloneUtils.clone(lastUpdateDate);
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = CloneUtils.clone(lastUpdateDate);
	}
	
	public Date getImportDate() {
		return CloneUtils.clone(importDate);
	}

	public void setImportDate(Date importDate) {
		this.importDate = CloneUtils.clone(importDate);
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
	public int compareTo(ArtifactVersion other) {
		if (this.equals(other)) {
			return 0;
		}
		return MavenCentralVersionComparator.reverse().compare(this, other);
	}
}
