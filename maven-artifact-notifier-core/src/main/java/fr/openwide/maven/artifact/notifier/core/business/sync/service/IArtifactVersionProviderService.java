package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import java.util.List;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;

public interface IArtifactVersionProviderService {

	List<ArtifactVersionBean> getArtifactVersions(String groupId, String artifactId) throws ServiceException;
}
