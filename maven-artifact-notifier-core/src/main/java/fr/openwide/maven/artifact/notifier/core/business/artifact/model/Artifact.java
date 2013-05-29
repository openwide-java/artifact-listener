package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.bindgen.Bindable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;
import fr.openwide.maven.artifact.notifier.core.business.artifact.util.ArtifactVersionLastUpdateDateComparator;

@Indexed
@Bindable
@Cacheable
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "group_id", "artifactId" }) })
public class Artifact extends GenericEntity<Long, Artifact> implements IArtifact {

	private static final long serialVersionUID = 8965262477865642972L;
	
	public static final String ARTIFACT_ID_SORT_FIELD_NAME = "artifactIdSort";
	
	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Index(name = "idx_Artifact_group_id")
	@IndexedEmbedded
	private ArtifactGroup group;
	
	@Column(nullable = false)
	@Index(name = "idx_Artifact_artifactId")
	@Fields({
		@Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT)),
		@Field(name = ARTIFACT_ID_SORT_FIELD_NAME, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT))
	})
	private String artifactId;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ArtifactStatus status = ArtifactStatus.NOT_INITIALIZED;
	
	@OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, orphanRemoval = true)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@Sort(type = SortType.NATURAL)
	private Set<ArtifactVersion> versions = Sets.newTreeSet();
	
	@OneToOne
	private ArtifactVersion latestVersion;
	
	@OneToOne
	private ArtifactVersion mostRecentVersion;
	
	@Column
	private Date creationDate;
	
	public Artifact() {
	}
	
	public Artifact(String artifactId) {
		this.artifactId = artifactId;
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

	public ArtifactGroup getGroup() {
		return group;
	}

	public void setGroup(ArtifactGroup group) {
		this.group = group;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public ArtifactStatus getStatus() {
		return status;
	}

	public void setStatus(ArtifactStatus status) {
		this.status = status;
	}

	public Set<ArtifactVersion> getVersions() {
		return Collections.unmodifiableSet(versions);
	}
	
	public void addVersion(ArtifactVersion version) {
		if (version != null) {
			versions.add(version);
			version.setArtifact(this);
			
			refreshCachedVersions();
		}
	}

	public void setVersions(Set<ArtifactVersion> versions) {
		this.versions.clear();
		this.versions.addAll(versions);
		
		refreshCachedVersions();
	}
	
	public ArtifactVersion getLatestVersion() {
		return latestVersion;
	}
	
	public void setLatestVersion(ArtifactVersion latestVersion) {
		this.latestVersion = latestVersion;
	}
	
	public ArtifactVersion getMostRecentVersion() {
		return mostRecentVersion;
	}

	public void setMostRecentVersion(ArtifactVersion mostRecentVersion) {
		this.mostRecentVersion = mostRecentVersion;
	}

	public void refreshCachedVersions() {
		latestVersion = Iterables.getFirst(versions, null);
		if (versions.size() > 0) {
			mostRecentVersion = ArtifactVersionLastUpdateDateComparator.get().max(versions);
		} else {
			mostRecentVersion = null;
		}
	}
	
	public Date getCreationDate() {
		return CloneUtils.clone(creationDate);
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = CloneUtils.clone(creationDate);
	}

	@Override
	public ArtifactKey getArtifactKey() {
		if (group == null) {
			return new ArtifactKey(null, null);
		}
		return new ArtifactKey(group.getGroupId(), artifactId);
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getNameForToString() {
		return artifactId;
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return artifactId;
	}

	@Override
	public int compareTo(Artifact other) {
		if (this.equals(other)) {
			return 0;
		}
		if (group != null) {
			int groupCompareResult = group.compareTo(other.getGroup());
			if (groupCompareResult != 0) {
				return groupCompareResult;
			}
		}
		return this.getArtifactId().compareTo(other.getArtifactId());
	}
}
