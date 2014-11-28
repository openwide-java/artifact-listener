package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IArtifactVersionNotificationDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Service("artifactVersionNotificationService")
public class ArtifactVersionNotificationServiceImpl extends GenericEntityServiceImpl<Long, ArtifactVersionNotification> implements IArtifactVersionNotificationService {

	private IArtifactVersionNotificationDao artifactVersionNotificationDao;
	
	@Autowired
	public ArtifactVersionNotificationServiceImpl(IArtifactVersionNotificationDao artifactVersionNotificationDao) {
		super(artifactVersionNotificationDao);
		this.artifactVersionNotificationDao = artifactVersionNotificationDao;
	}
	
	@Override
	public List<ArtifactVersionNotification> listByArtifact(Artifact artifact) {
		return artifactVersionNotificationDao.listByArtifact(artifact);
	}
	
	@Override
	public Map<User, List<ArtifactVersionNotification>> listNotificationsToSend() {
		return artifactVersionNotificationDao.listNotificationsToSend();
	}
}
