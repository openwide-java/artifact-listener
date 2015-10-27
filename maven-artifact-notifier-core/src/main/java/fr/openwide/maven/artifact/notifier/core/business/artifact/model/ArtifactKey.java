package fr.openwide.maven.artifact.notifier.core.business.artifact.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bindgen.Bindable;

import fr.openwide.core.spring.util.StringUtils;

@Bindable
public class ArtifactKey implements Serializable, Comparable<ArtifactKey> {
	
	private static final long serialVersionUID = -8648696527579176730L;
	
	private static final String ID_SEPARATOR = ":";
	
	private String groupId;
	
	private String artifactId;
	
	private String key;
	
	public ArtifactKey(String key) {
		this.key = key;
		if (StringUtils.hasText(key)) {
			int separatorIndex = key.indexOf(ID_SEPARATOR);
			if (separatorIndex > -1) {
				this.groupId = key.substring(0, separatorIndex);
				this.artifactId = key.substring(separatorIndex + 1);
			}
		}
	}
	
	public ArtifactKey(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		if (StringUtils.hasText(groupId) && StringUtils.hasText(artifactId)) {
			this.key = groupId + ID_SEPARATOR + artifactId;
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
