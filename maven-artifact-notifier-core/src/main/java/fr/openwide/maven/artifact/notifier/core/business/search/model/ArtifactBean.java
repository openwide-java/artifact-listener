package fr.openwide.maven.artifact.notifier.core.business.search.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.solr.client.solrj.beans.Field;
import org.bindgen.Bindable;

import com.google.common.collect.Lists;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.IArtifact;
import fr.openwide.maven.artifact.notifier.core.business.search.util.MavenCentralSearchApiConstants;

@Bindable
public class ArtifactBean implements IArtifact, Serializable, Comparable<ArtifactBean> {

	private static final long serialVersionUID = -8568482584304757945L;
	
	@Field
	private String id;

	@Field(MavenCentralSearchApiConstants.ARTIFACT_FIELD)
	private String artifactId;

	@Field(MavenCentralSearchApiConstants.GROUP_FIELD)
	private String groupId;

	@Field
	private List<String> ec = Lists.newArrayList();

	@Field
	private String latestVersion;

	@Field(MavenCentralSearchApiConstants.TYPE_FIELD)
	private String type;

	@Field
	private String repositoryId;

	@Field
	private List<String> text = Lists.newArrayList();

	@Field
	private Long timestamp;

	@Field
	private int versionCount;

	public ArtifactBean() {
	}

	public ArtifactBean(FollowedArtifact followedArtifact) {
		if (followedArtifact != null) {
			setArtifactId(followedArtifact.getArtifact().getArtifactId());
			setGroupId(followedArtifact.getArtifact().getGroup().getGroupId());
			ArtifactVersion artifactLatestVersion = followedArtifact.getArtifact().getLatestVersion();
			if (artifactLatestVersion != null) {
				setLatestVersion(artifactLatestVersion.getVersion());
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getEc() {
		return ec;
	}

	public void setEc(List<String> ec) {
		this.ec = ec;
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public List<String> getText() {
		return text;
	}

	public void setText(List<String> text) {
		this.text = text;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public int getVersionCount() {
		return versionCount;
	}

	public void setVersionCount(int versionCount) {
		this.versionCount = versionCount;
	}

	@Override
	public ArtifactKey getArtifactKey() {
		return new ArtifactKey(groupId, artifactId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ArtifactBean) {
			ArtifactBean artifactBean = (ArtifactBean) obj;
			return this.getId().equals(artifactBean.getId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public int compareTo(ArtifactBean other) {
		if (this.equals(other)) {
			return 0;
		}
		if (groupId != null) {
			int groupCompareResult = groupId.compareTo(other.getGroupId());
			if (groupCompareResult != 0) {
				return groupCompareResult;
			}
		}
		return this.getArtifactId().compareTo(other.getArtifactId());
	}
}
