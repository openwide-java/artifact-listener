package fr.openwide.maven.artifact.notifier.core.business.project.model;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.bindgen.Bindable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.audit.model.AuditSummary;

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
	
	@Column(nullable = false, unique = true)
	private String uri;
	
	@Embedded
	private ItemAdditionalInformation additionalInformation = new ItemAdditionalInformation();
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@SortNatural
	private Set<ProjectVersion> versions = Sets.newTreeSet();
	
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	@SortNatural
	private Set<Artifact> artifacts = Sets.newTreeSet();
	
	@Embedded
	private AuditSummary auditSummary;
	
	public Project() {
	}

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

	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = StringUtils.urlize(uri);
	}
	
	public ItemAdditionalInformation getAdditionalInformation() {
		if (additionalInformation == null) {
			additionalInformation = new ItemAdditionalInformation();
		}
		return additionalInformation;
	}

	public void setAdditionalInformation(ItemAdditionalInformation additionalInformation) {
		this.additionalInformation = additionalInformation;
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
	
	public void removeVersion(ProjectVersion version) {
		versions.remove(version);
		version.setProject(null);
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
	
	public void removeArtifact(Artifact artifact) {
		artifacts.remove(artifact);
		artifact.setProject(null);
	}

	public void setArtifacts(Set<Artifact> artifacts) {
		this.artifacts.clear();
		this.artifacts.addAll(artifacts);
	}
	
	public AuditSummary getAuditSummary() {
		if (auditSummary == null) {
			auditSummary = new AuditSummary();
		}
		return auditSummary;
	}

	@Override
	@JsonIgnore
	public String getNameForToString() {
		return name;
	}

	@Override
	@JsonIgnore
	public String getDisplayName() {
		return name;
	}

}