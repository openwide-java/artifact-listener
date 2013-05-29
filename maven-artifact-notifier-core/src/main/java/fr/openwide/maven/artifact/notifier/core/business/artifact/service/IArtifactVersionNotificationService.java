package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;

public interface IArtifactVersionNotificationService extends IGenericEntityService<Long, ArtifactVersionNotification> {

	List<ArtifactVersionNotification> listByArtifact(Artifact artifact);
}
