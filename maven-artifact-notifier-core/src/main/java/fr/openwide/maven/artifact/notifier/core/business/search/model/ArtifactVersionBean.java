package fr.openwide.maven.artifact.notifier.core.business.search.model;

import java.io.Serializable;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class ArtifactVersionBean implements Serializable {

	private static final long serialVersionUID = -1267507142034477945L;

	@Field
	private String id;
	
	@Field("a")
	private String artifactId;
	
	@Field("g")
	private String groupId;
	
	@Field
	private List<String> ec;
	
	@Field("p")
	private String type;
	
	@Field
	private List<String> tags;
	
	@Field
	private Long timestamp;
	
	@Field("v")
	private String version;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
