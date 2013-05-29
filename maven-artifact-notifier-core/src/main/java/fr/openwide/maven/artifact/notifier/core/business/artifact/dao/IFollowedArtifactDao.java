package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import java.util.List;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public interface IFollowedArtifactDao extends IGenericEntityDao<Long, FollowedArtifact> {

	List<User> listFollowers(Artifact artifact);
}
