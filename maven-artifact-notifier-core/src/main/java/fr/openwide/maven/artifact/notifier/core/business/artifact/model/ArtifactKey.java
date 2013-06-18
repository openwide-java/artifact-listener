package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bindgen.Bindable;

import fr.openwide.core.spring.util.StringUtils;

@Bindable
public class ArtifactKey implements Serializable, Comparable<ArtifactKey> {
	
	private static final long serialVersionUID = -8648696527579176730L;
	
	private String groupId;
	
	private String artifactId;
	
	private String key;
	
	public ArtifactKey(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		if (StringUtils.hasText(groupId) && StringUtils.hasText(artifactId)) {
			this.key = groupId + ":" + artifactId;
		}
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	
	public String getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object instanceof ArtifactKey) {
			ArtifactKey artifactKey = (ArtifactKey) object;
			return this.getKey().equals(artifactKey.getKey());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(key).toHashCode();
	}

	@Override
	public int compareTo(ArtifactKey other) {
		if (this.equals(other)) {
			return 0;
		}
		if (this.getKey() == null) {
			return -1;
		}
		if (other.getKey() == null) {
			return 1;
		}
		return this.getKey().compareTo(other.getKey());
	}
}
