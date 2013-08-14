package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;

public interface IArtifactVersionService extends IGenericEntityService<Long, ArtifactVersion> {
	
	List<ArtifactVersion> listByArtifact(Artifact artifact);
	
	List<ArtifactVersion> listByProjectVersion(ProjectVersion projectVersion);

	ArtifactVersion getByArtifactAndVersion(Artifact artifact, String version);

	List<ArtifactVersion> listRecentReleases(int limit);
}
