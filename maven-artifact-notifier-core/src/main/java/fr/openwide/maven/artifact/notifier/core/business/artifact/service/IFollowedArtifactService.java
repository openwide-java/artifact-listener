package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleType;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public interface IFollowedArtifactService extends IGenericEntityService<Long, FollowedArtifact> {

	List<FollowedArtifact> listByArtifact(Artifact artifact);

	List<User> listFollowers(Artifact artifact);

	List<ArtifactNotificationRule> listArtifactNotificationRules(FollowedArtifact followedArtifact);
	
	void addArtifactNotificationRule(FollowedArtifact followedArtifact, String regex,
			ArtifactNotificationRuleType type) throws ServiceException, SecurityServiceException;

	void removeArtifactNotificationRule(FollowedArtifact followedArtifact, ArtifactNotificationRule rule)
			throws ServiceException, SecurityServiceException;

	void deleteNotifications(Artifact artifact) throws ServiceException, SecurityServiceException;
}
