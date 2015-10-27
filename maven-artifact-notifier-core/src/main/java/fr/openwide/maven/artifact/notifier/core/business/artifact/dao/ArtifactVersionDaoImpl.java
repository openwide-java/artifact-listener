package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.QArtifactVersion;

@Repository("artifactVersionDao")
public class ArtifactVersionDaoImpl extends GenericEntityDaoImpl<Long, ArtifactVersion> implements IArtifactVersionDao {

	private static final QArtifactVersion qArtifactVersion = QArtifactVersion.artifactVersion;
	
	@Override
	public ArtifactVersion getByArtifactAndVersion(Artifact artifact, String version) {
		JPAQuery<ArtifactVersion> query = new JPAQuery<>(getEntityManager());
		
		query.select(qArtifactVersion)
			.from(qArtifactVersion)
			.where(qArtifactVersion.artifact.eq(artifact),
					qArtifactVersion.version.eq(version));
		
		return query.fetchOne();
	}
	
	@Override
	public List<ArtifactVersion> listRecentReleases(int limit) {
		JPAQuery<ArtifactVersion> query = new JPAQuery<>(getEntityManager());
		
		query.select(qArtifactVersion)
			.from(qArtifactVersion)
			.where(qArtifactVersion.artifact.deprecationStatus.eq(ArtifactDeprecationStatus.NORMAL))
			.orderBy(qArtifactVersion.lastUpdateDate.desc())
			.limit(limit);
		
		return query.fetch();
	}
}
