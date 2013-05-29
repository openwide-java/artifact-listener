package fr.openwide.maven.artifact.notifier.core.business.user.dao;

import java.util.Date;
import java.util.List;

import fr.openwide.core.jpa.security.business.person.dao.IPersonDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public interface IUserDao extends IPersonDao<User> {

	FollowedArtifact getFollowedArtifact(User user, Artifact artifact);

	List<ArtifactVersionNotification> listLastNotifications(User user, long limit);

	List<ArtifactVersionNotification> listNotificationsAfterDate(User user, Date date);
}
