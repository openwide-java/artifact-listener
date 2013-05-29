package fr.openwide.maven.artifact.notifier.core.business.search.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.bindgen.Bindable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Bindable
public class PomBean implements Serializable {

	private static final long serialVersionUID = -3374940419116249995L;
	
	private String groupId;
	
	private String artifactId;
	
	private List<ArtifactBean> dependencies = Lists.newArrayList();
	
	private List<ArtifactBean> dependencyManagement = Lists.newArrayList();
	
	private List<ArtifactBean> plugins = Lists.newArrayList();
	
	private List<ArtifactBean> pluginManagement = Lists.newArrayList();
	
	private Set<ArtifactBean> invalidArtifacts = Sets.newTreeSet();
	
	public PomBean() {
	}
	
	public PomBean(PomBean copy) {
		if (copy != null) {
			setGroupId(copy.getGroupId());
			setArtifactId(copy.getArtifactId());
		}
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	public List<ArtifactBean> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<ArtifactBean> dependencies) {
		this.dependencies = dependencies;
	}
	
	public List<ArtifactBean> getDependencyManagement() {
		return dependencyManagement;
	}
	
	public void setDependencyManagement(List<ArtifactBean> dependencyManagement) {
		this.dependencyManagement = dependencyManagement;
	}
	
	public List<ArtifactBean> getPlugins() {
		return plugins;
	}
	
	public void setPlugins(List<ArtifactBean> plugins) {
		this.plugins = plugins;
	}
	
	public List<ArtifactBean> getPluginManagement() {
		return pluginManagement;
	}
	
	public void setPluginManagement(List<ArtifactBean> pluginManagement) {
		this.pluginManagement = pluginManagement;
	}
	
	public Set<ArtifactBean> getInvalidArtifacts() {
		return invalidArtifacts;
	}
	
	public void setInvalidArtifacts(Set<ArtifactBean> invalidArtifacts) {
		this.invalidArtifacts = invalidArtifacts;
	}
}
