package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import org.springframework.stereotype.Repository;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;

@Repository("artifactGroupDao")
public class ArtifactGroupDaoImpl extends GenericEntityDaoImpl<Long, ArtifactGroup> implements IArtifactGroupDao {

}
