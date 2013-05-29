package fr.openwide.maven.artifact.notifier.core.business.artifact.dao;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;

public interface IArtifactNotificationRuleDao extends IGenericEntityDao<Long, ArtifactNotificationRule> {

	ArtifactNotificationRule getByFollowedArtifactAndRegex(FollowedArtifact followedArtifact, String regex);
}
