package fr.openwide.maven.artifact.notifier.core.business.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

@Service("mavenCentralSearchUrlService")
public class MavenCentralSearchUrlServiceImpl implements IMavenCentralSearchUrlService {

	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Override
	public String getGroupUrl(String groupId) {
		if (groupId == null) {
			return null;
		}
		return String.format(configurer.getMavenCentralSearchUrlGroup(), groupId);
	}

	@Override
	public String getArtifactUrl(String groupId, String artifactId) {
		if (artifactId == null) {
			return null;
		}
		return String.format(configurer.getMavenCentralSearchUrlArtifact(), groupId, artifactId);
	}
	
	@Override
	public String getVersionUrl(String groupId, String artifactId, String version) {
		return String.format(configurer.getMavenCentralSearchUrlVersion(), groupId, artifactId, version);
	}

	@Override
	public String getVersionUrl(ArtifactVersion version) {
		if (version == null) {
			return null;
		}
		return getVersionUrl(
				version.getArtifact().getGroup().getGroupId(),
				version.getArtifact().getArtifactId(),
				version.getVersion()
		);
	}
}
