package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.Date;
import java.util.List;

import org.apache.lucene.search.SortField;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public interface IArtifactDao extends IGenericEntityDao<Long, Artifact> {

	List<ArtifactVersion> listArtifactVersionsAfterDate(Artifact artifact, Date date);
	
	Artifact getByGroupIdArtifactId(String groupId, String artifactId);

	List<Artifact> searchByName(String searchTerm, List<SortField> sort, Integer limit, Integer offset);

	int countSearchByName(String searchTerm);
}
