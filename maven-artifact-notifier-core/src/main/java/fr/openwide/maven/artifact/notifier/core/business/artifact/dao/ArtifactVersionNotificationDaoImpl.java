package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mysema.query.group.GroupBy;
import com.mysema.query.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotificationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Repository("artifactVersionNotificationDao")
public class ArtifactVersionNotificationDaoImpl extends GenericEntityDaoImpl<Long, ArtifactVersionNotification> implements IArtifactVersionNotificationDao {

	private static final QArtifactVersionNotification qArtifactVersionNotification = QArtifactVersionNotification.artifactVersionNotification;
	
	public ArtifactVersionNotificationDaoImpl() {
		super();
	}
	
	@Override
	public List<ArtifactVersionNotification> listByArtifact(Artifact artifact) {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		query.from(qArtifactVersionNotification)
			.where(qArtifactVersionNotification.artifactVersion.artifact.eq(artifact))
			.orderBy(qArtifactVersionNotification.creationDate.desc());
		
		return query.list(qArtifactVersionNotification);
	}

	@Override
	public Map<User, List<ArtifactVersionNotification>> listNotificationsToSend() {
		JPAQuery query = new JPAQuery(getEntityManager());
		
		return query.from(qArtifactVersionNotification)
				.where(qArtifactVersionNotification.status.eq(ArtifactVersionNotificationStatus.PENDING))
				.orderBy(qArtifactVersionNotification.user.id.asc(), qArtifactVersionNotification.id.asc())
				.transform(GroupBy.groupBy(qArtifactVersionNotification.user).as(GroupBy.list(qArtifactVersionNotification)));
	}
}
