package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.List;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public interface IArtifactVersionDao extends IGenericEntityDao<Long, ArtifactVersion> {

	ArtifactVersion getByArtifactAndVersion(Artifact artifact, String version);

	List<ArtifactVersion> listRecentReleases(int limit);
}
