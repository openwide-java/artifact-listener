package fr.openwide.maven.artifact.notifier.core.business.user.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.security.business.person.dao.AbstractPersonDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QFollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Repository("personDao")
public class UserDaoImpl extends AbstractPersonDaoImpl<User> implements IUserDao {

	private static final QArtifactVersionNotification qArtifactVersionNotification = QArtifactVersionNotification.artifactVersionNotification;
	
	private static final QFollowedArtifact qFollowedArtifact = QFollowedArtifact.followedArtifact;
	
	public UserDaoImpl() {
		super();
	}
	
	@Override
	public List<ArtifactVersionNotification> listLastNotifications(User user, long limit) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifactVersionNotification)
			.where(qArtifactVersionNotification.user.eq(user))
			.orderBy(qArtifactVersionNotification.creationDate.desc())
			.limit(limit);
		
		return query.list(qArtifactVersionNotification);
	}
	
	@Override
	public List<ArtifactVersionNotification> listNotificationsAfterDate(User user, Date date) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifactVersionNotification)
			.where(qArtifactVersionNotification.user.eq(user))
			.where(qArtifactVersionNotification.creationDate.after(date))
			.orderBy(qArtifactVersionNotification.creationDate.desc());
		
		return query.list(qArtifactVersionNotification);
	}
	
	@Override
	public FollowedArtifact getFollowedArtifact(User user, Artifact artifact) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qFollowedArtifact)
			.where(qFollowedArtifact.user.eq(user))
			.where(qFollowedArtifact.artifact.eq(artifact));
		
		return query.singleResult(qFollowedArtifact);
	}
}
