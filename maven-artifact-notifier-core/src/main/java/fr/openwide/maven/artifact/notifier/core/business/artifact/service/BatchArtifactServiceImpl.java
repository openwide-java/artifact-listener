package fr.openwide.maven.artifact.notifier.core.business.artifact.service;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotificationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationService;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersionStatus;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectVersionService;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic;
import fr.openwide.maven.artifact.notifier.core.business.statistics.service.IStatisticService;
import fr.openwide.maven.artifact.notifier.core.business.sync.service.IArtifactVersionProviderService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("batchArtifactService")
public class BatchArtifactServiceImpl implements IBatchArtifactService {

	private static final Logger LOG = LoggerFactory.getLogger(BatchArtifactServiceImpl.class);

	@Autowired
	private IFollowedArtifactService followedArtifactService;

	@Autowired
	private IArtifactVersionProviderService artifactVersionProviderService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IArtifactNotificationRuleService artifactNotificationRuleService;

	@Autowired
	private IArtifactVersionService artifactVersionService;

	@Autowired
	private IArtifactService artifactService;

	@Autowired
	private IProjectVersionService projectVersionService;

	@Autowired
	private IProjectService projectService;

	@Autowired
	private IArtifactVersionNotificationService artifactVersionNotificationService;

	@Autowired
	private INotificationService notificationService;

	@Autowired
	private IStatisticService statisticService;

	@Override
	public void synchronizeArtifact(Long artifactId, MutableInt versionsReleasedCount) throws ServiceException, SecurityServiceException {
		Artifact artifact = artifactService.getById(artifactId);

		LOG.info("Synchronizing artifact {}", artifact);
		List<ArtifactVersionBean> versions = artifactVersionProviderService.getArtifactVersions(artifact.getGroup().getGroupId(),
				artifact.getArtifactId());

		int newVersionsCount = addNewVersions(artifact, versions);
		LOG.info("{} new versions for artifact {}", newVersionsCount, artifact);
		if (ArtifactStatus.NOT_INITIALIZED.equals(artifact.getStatus())) {
			if (artifact.getMostRecentVersion() != null) {
				Date lastUpdateDate = artifact.getMostRecentVersion().getLastUpdateDate();

				List<FollowedArtifact> followedArtifacts = followedArtifactService.listByArtifact(artifact);
				for (FollowedArtifact followedArtifact : followedArtifacts) {
					followedArtifact.setLastNotifiedVersionDate(lastUpdateDate);
				}
			}

			artifact.setStatus(ArtifactStatus.INITIALIZED);
			artifactService.update(artifact);
		} else {
			if (newVersionsCount > 0) {
				versionsReleasedCount.add(newVersionsCount);
				LOG.info("Creating notifications for artifact {}", artifact);
				createNotificationsForUsers(artifact);
			}
		}
	}

	@Override
	public void notifyUsers() throws ServiceException, SecurityServiceException {
		Map<User, List<ArtifactVersionNotification>> notificationsToSend = artifactVersionNotificationService.listNotificationsToSend();
		LOG.info("Sending notifications to {} users", notificationsToSend.size());

		int notificationsSent = 0;
		for (Map.Entry<User, List<ArtifactVersionNotification>> entry : notificationsToSend.entrySet()) {
			User user = entry.getKey();
			LOG.info("Sending notifications to {}", user);

			List<ArtifactVersionNotification> notifications = entry.getValue();
			Collections.sort(notifications);
			notificationService.sendNewVersionNotification(notifications, user);

			for (ArtifactVersionNotification notification : notifications) {
				notification.setStatus(ArtifactVersionNotificationStatus.SENT);
				artifactVersionNotificationService.update(notification);
			}

			notificationsSent += notifications.size();
		}
		statisticService.feed(Statistic.StatisticEnumKey.NOTIFICATIONS_SENT_PER_DAY, notificationsSent);
	}

	private int addNewVersions(Artifact artifact, List<ArtifactVersionBean> versions) throws ServiceException, SecurityServiceException {
		int newVersionsCount = 0;

		for (ArtifactVersionBean versionBean : versions) {
			ArtifactVersion artifactVersion = artifactVersionService.getByArtifactAndVersion(artifact, versionBean.getVersion());

			if (artifactVersion == null) {
				artifactVersion = new ArtifactVersion(versionBean.getVersion(), new Date(versionBean.getTimestamp()));
				// it is necessary to create the version before adding it to the artifact because, otherwise, we have a cascading problem with latestVersion
				artifactVersionService.create(artifactVersion);

				artifact.addVersion(artifactVersion);
				artifactService.update(artifact);

				// we optionally push the version into the project
				pushNewVersionIntoProject(artifact, artifactVersion);
				++newVersionsCount;
			}
		}
		return newVersionsCount;
	}


	private void createNotificationsForUsers(Artifact artifact) throws ServiceException, SecurityServiceException {
		List<FollowedArtifact> followedArtifacts = followedArtifactService.listByArtifact(artifact);

		int nbFollowers = followedArtifacts.size();
		int i = 1;
		LOG.info("{} users are listening to artifact {}", nbFollowers, artifact);
		for (FollowedArtifact followedArtifact : followedArtifacts) {
			LOG.info("Processing notification for user {} regarding artifact {} ({}/{} {}%)", followedArtifact.getUser(), artifact, i, nbFollowers, i * 100 / nbFollowers);
            i++;
			
			createNotifications(followedArtifact);
		}
	}

	private void createNotifications(FollowedArtifact followedArtifact) throws ServiceException, SecurityServiceException {
		if (followedArtifact.getLastNotifiedVersionDate() == null) {
			// this case shouldn't happen but it's better to be on the safe side rather than sending a bunch of unwanted notifications...
			followedArtifact.setLastNotifiedVersionDate(new Date());
			followedArtifactService.update(followedArtifact);
			return;
		}

		User follower = followedArtifact.getUser();

		if (!follower.isActive()) {
			return;
		}

		Artifact artifact = followedArtifact.getArtifact();
		LOG.info("Creating notification for user {} regarding artifact {}", follower, artifact);

		List<ArtifactVersion> lastVersions = artifactService.listArtifactVersionsAfterDate(artifact, followedArtifact.getLastNotifiedVersionDate());

		List<ArtifactNotificationRule> rules = followedArtifactService.listArtifactNotificationRules(followedArtifact);

		for (ArtifactVersion version : lastVersions) {
			// Rules check
			if (!artifactNotificationRuleService.checkRulesForVersion(version.getVersion(), rules)) {
				continue;
			}

			ArtifactVersionNotification notification = new ArtifactVersionNotification(version);
			artifactVersionNotificationService.create(notification);

			follower.addNotification(notification);
			if (followedArtifact.getLastNotifiedVersionDate().before(version.getLastUpdateDate())) {
				followedArtifact.setLastNotifiedVersionDate(version.getLastUpdateDate());
			}
		}
		if (!lastVersions.isEmpty()) {
			userService.update(follower);
		}
	}

	private void pushNewVersionIntoProject(Artifact artifact, ArtifactVersion artifactVersion) throws ServiceException, SecurityServiceException {
		if (artifact.getProject() != null) {
			Project project = artifact.getProject();
			ProjectVersion projectVersion = projectVersionService.getByProjectAndVersion(artifact.getProject(), artifactVersion.getVersion());

			if (projectVersion == null) {
				projectVersion = new ProjectVersion(artifactVersion.getVersion());
				projectVersion.setStatus(ProjectVersionStatus.PUBLISHED_ON_MAVEN_CENTRAL);
				projectVersionService.create(projectVersion);

				project.addVersion(projectVersion);
				projectService.update(project);
			} else if (ProjectVersionStatus.IN_PROGRESS.equals(projectVersion.getStatus())) {
				projectVersion.setStatus(ProjectVersionStatus.PUBLISHED_ON_MAVEN_CENTRAL);
			}
			projectVersion.setLastUpdateDate(artifactVersion.getLastUpdateDate());
			projectVersionService.update(projectVersion);

			artifactVersion.setProjectVersion(projectVersion);
			artifactVersionService.update(artifactVersion);
		}
	}

}
