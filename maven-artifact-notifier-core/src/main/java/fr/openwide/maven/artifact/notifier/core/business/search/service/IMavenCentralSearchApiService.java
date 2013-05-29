package fr.openwide.maven.artifact.notifier.core.business.search.service;

import java.io.File;
import java.util.List;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;

public interface IMavenCentralSearchApiService {
	
	List<ArtifactBean> getArtifacts(String global, String groupId, String artifactId, int offset, int maxRows)
			throws ServiceException;

	long countArtifacts(String global, String groupId, String artifactId) throws ServiceException;

	long countArtifacts(String artifactId) throws ServiceException;

	PomBean searchFromPom(String xml) throws ServiceException;
	
	PomBean searchFromPom(File file) throws ServiceException;

	List<ArtifactVersionBean> getArtifactVersions(String groupId, String artifactId) throws ServiceException;
}
