package fr.openwide.maven.artifact.notifier.core.business.project.model;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.bindgen.Bindable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;

@Indexed
@Bindable
@Cacheable
@Entity
public class Project extends GenericEntity<Long, Project> {
	
	private static final long serialVersionUID = 2367269665391314255L;

	public static final String NAME_SORT_FIELD_NAME = "nameSort";
	
	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@Column(nullable = false, unique = true)
	@Fields({
		@Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT)),
		@Field(name = NAME_SORT_FIELD_NAME, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT))
	})
	private String name;
	
	@Column
	private String websiteUrl;
	
	@Column
	private String issueTrackerUrl;
	
	@Column
	private String scmUrl;
	
	@Column
	private String changelogUrl;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Sort(type = SortType.NATURAL)
	private Set<ProjectLicense> licenses = Sets.newTreeSet();
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@Sort(type = SortType.NATURAL)
	private Set<ProjectVersion> versions = Sets.newTreeSet();
	
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	@Sort(type = SortType.NATURAL)
	private Set<Artifact> artifacts = Sets.newTreeSet();
	
	@Column(nullable = false)
	private Date creationDate;
	
	@Column(nullable = false)
	private Date lastUpdateDate;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	
	public String getScmUrl() {
		return scmUrl;
	}

	public void setScmUrl(String scmUrl) {
		this.scmUrl = scmUrl;
	}

	public String getIssueTrackerUrl() {
		return issueTrackerUrl;
	}

	public void setIssueTrackerUrl(String issueTrackerUrl) {
		this.issueTrackerUrl = issueTrackerUrl;
	}

	public String getChangelogUrl() {
		return changelogUrl;
	}

	public void setChangelogUrl(String changelogUrl) {
		this.changelogUrl = changelogUrl;
	}

	public Set<ProjectLicense> getLicenses() {
		return Collections.unmodifiableSet(licenses);
	}
	
	public void addLicense(ProjectLicense license) {
		if (license != null) {
			licenses.add(license);
		}
	}

	public void setLicenses(Set<ProjectLicense> licenses) {
		this.licenses.clear();
		this.licenses.addAll(licenses);
	}

	public Set<ProjectVersion> getVersions() {
		return Collections.unmodifiableSet(versions);
	}
	
	public void addVersion(ProjectVersion version) {
		if (version != null) {
			versions.add(version);
			version.setProject(this);
		}
	}

	public void setVersions(Set<ProjectVersion> versions) {
		this.versions.clear();
		this.versions.addAll(versions);
	}

	public Set<Artifact> getArtifacts() {
		return Collections.unmodifiableSet(artifacts);
	}
	
	public void addArtifact(Artifact artifact) {
		if (artifact != null) {
			artifacts.add(artifact);
			artifact.setProject(this);
		}
	}

	public void setArtifacts(Set<Artifact> artifacts) {
		this.artifacts.clear();
		this.artifacts.addAll(artifacts);
	}
	
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
		return name;
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return name;
	}

}