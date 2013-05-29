package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifactVersionNotification;

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
}
