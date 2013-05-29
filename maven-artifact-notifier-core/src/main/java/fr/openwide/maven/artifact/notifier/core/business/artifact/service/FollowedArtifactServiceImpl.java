package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.dao.IFollowedArtifactDao;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleType;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact_;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Service("followedArtifactService")
public class FollowedArtifactServiceImpl extends GenericEntityServiceImpl<Long, FollowedArtifact> implements IFollowedArtifactService {

	private IFollowedArtifactDao followedArtifactDao;

	@Autowired
	private IArtifactVersionNotificationService artifactVersionNotificationService;
	
	@Autowired
	private IArtifactNotificationRuleService artifactNotificationRuleService;

	@Autowired
	public FollowedArtifactServiceImpl(IFollowedArtifactDao followedArtifactDao) {
		super(followedArtifactDao);

		this.followedArtifactDao = followedArtifactDao;
	}

	@Override
	public List<FollowedArtifact> listByArtifact(Artifact artifact) {
		return listByField(FollowedArtifact_.artifact, artifact);
	}

	@Override
	public List<User> listFollowers(Artifact artifact) {
		return followedArtifactDao.listFollowers(artifact);
	}
	
	@Override
	public List<ArtifactNotificationRule> listArtifactNotificationRules(FollowedArtifact followedArtifact) {
		return followedArtifact.getArtifactNotificationRules();
	}
	
	@Override
	public void addArtifactNotificationRule(FollowedArtifact followedArtifact, String regex, ArtifactNotificationRuleType type)
			throws ServiceException, SecurityServiceException {
		ArtifactNotificationRule rule = new ArtifactNotificationRule(followedArtifact, regex, type);
		artifactNotificationRuleService.create(rule);
		
		followedArtifact.addArtifactNotificationRule(rule);
		update(followedArtifact);
	}
	
	@Override
	public void removeArtifactNotificationRule(FollowedArtifact followedArtifact, ArtifactNotificationRule rule)
			throws ServiceException, SecurityServiceException {
		followedArtifact.getArtifactNotificationRules().remove(rule);
		update(followedArtifact);
		
		artifactNotificationRuleService.delete(rule);
	}
	
	@Override
	public void deleteNotifications(Artifact artifact) throws ServiceException, SecurityServiceException {
		List<ArtifactVersionNotification> notifications = artifactVersionNotificationService.listByArtifact(artifact);
		for (ArtifactVersionNotification notification : notifications) {
			notification.getUser().getNotifications().remove(notification);
			artifactVersionNotificationService.delete(notification);
		}
	}
}
