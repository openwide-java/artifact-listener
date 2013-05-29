package fr.openwide.maven.artifact.notifier.core.business.search.service;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public interface IMavenCentralSearchUrlService {
	
	String getGroupUrl(String groupId);

	String getArtifactUrl(String groupId, String artifactId);

	String getVersionUrl(String groupId, String artifactId, String version);

	String getVersionUrl(ArtifactVersion version);
}
