package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.List;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;

public interface IArtifactVersionNotificationDao extends IGenericEntityDao<Long, ArtifactVersionNotification> {

	List<ArtifactVersionNotification> listByArtifact(Artifact artifact);
}
