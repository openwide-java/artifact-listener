package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.bindgen.Bindable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;

@Indexed
@Bindable
@Cacheable
@Entity
public class ArtifactGroup extends GenericEntity<Long, ArtifactGroup> {

	private static final long serialVersionUID = -170270018380208696L;
	
	public static final String GROUP_ID_SORT_FIELD_NAME = "groupIdSort";

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@Column(nullable = false, unique = true)
	@NaturalId
	@Fields({
		@Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT)),
		@Field(name = GROUP_ID_SORT_FIELD_NAME, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT))
	})
	@SortableField(forField = GROUP_ID_SORT_FIELD_NAME)
	private String groupId;
	
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@ContainedIn
	private List<Artifact> artifacts = Lists.newArrayList();
	
	public ArtifactGroup() {
	}
	
	public ArtifactGroup(String groupId) {
		this.groupId = groupId;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<Artifact> getArtifacts() {
		return Collections.unmodifiableList(artifacts);
	}
	
	public void addArtifact(Artifact artifact) {
		if (!artifacts.contains(artifact)) {
			artifacts.add(artifact);
			artifact.setGroup(this);
		}
	}

	public void setArtifacts(Collection<Artifact> artifacts) {
		this.artifacts.clear();
		this.artifacts.addAll(artifacts);
	}

	@Override
	@JsonIgnore
	public String getNameForToString() {
		return groupId;
	}

	@Override
	@JsonIgnore
	public String getDisplayName() {
		return groupId;
	}
	
	@Override
	public int compareTo(ArtifactGroup other) {
		if (this.equals(other)) {
			return 0;
		}
		return this.getGroupId().compareTo(other.getGroupId());
	}
}
