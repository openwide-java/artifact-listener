package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleType;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;

public interface IArtifactNotificationRuleService extends IGenericEntityService<Long, ArtifactNotificationRule> {

	ArtifactNotificationRule getByFollowedArtifactAndRegex(FollowedArtifact followedArtifact, String regex);

	boolean isRuleValid(String regex);

	void changeRuleType(ArtifactNotificationRule rule, ArtifactNotificationRuleType type) throws ServiceException,
	SecurityServiceException;

	boolean checkRulesForVersion(String version, List<ArtifactNotificationRule> rules);
}
