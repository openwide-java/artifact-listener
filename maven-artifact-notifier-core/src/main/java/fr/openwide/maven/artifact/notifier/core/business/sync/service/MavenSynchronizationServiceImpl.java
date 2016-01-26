package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.spring.property.service.IPropertyService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotificationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactNotificationRuleService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionNotificationService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationService;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersionStatus;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectVersionService;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactVersionBean;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic.StatisticEnumKey;
import fr.openwide.maven.artifact.notifier.core.business.statistics.service.IStatisticService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.core.property.MavenArtifactNotifierCorePropertyIds;

@Service("mavenSynchronizationService")
public class MavenSynchronizationServiceImpl implements IMavenSynchronizationService {
	
	private static final int BATCH_PARTITION_SIZE = 50;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IArtifactService artifactService;
	
	@Autowired
	private IArtifactVersionService artifactVersionService;
	
	@Autowired
	private IArtifactVersionNotificationService artifactVersionNotificationService;
	
	@Autowired
	private IFollowedArtifactService followedArtifactService;
	
	@Autowired
	private INotificationService notificationService;
	
	@Autowired
	private IArtifactVersionProviderService artifactVersionProviderService;
	
	@Autowired
	private IArtifactNotificationRuleService artifactNotificationRuleService;
	
	@Autowired
	private IStatisticService statisticService;
	
	@Autowired
	private IProjectService projectService;
	
	@Autowired
	private IProjectVersionService projectVersionService;
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Autowired
	private IPropertyService propertyService;
	
	@Override
	public void initializeAllArtifacts() throws ServiceException, SecurityServiceException, InterruptedException {
		synchronizeArtifacts(artifactService.listIdsByStatus(ArtifactStatus.NOT_INITIALIZED));
	}
	
	@Override
	public void synchronizeArtifactsAndNotifyUsers(List<Long> artifactIds) throws ServiceException, SecurityServiceException, InterruptedException {
		synchronizeArtifacts(artifactIds);
		
		int notificationsSent = 0;
		for (Map.Entry<User, List<ArtifactVersionNotification>> entry : artifactVersionNotificationService.listNotificationsToSend().entrySet()) {
			List<ArtifactVersionNotification> notifications = entry.getValue();
			Collections.sort(notifications);
			notificationService.sendNewVersionNotification(notifications, entry.getKey());
			
			for (ArtifactVersionNotification notification : notifications) {
				notification.setStatus(ArtifactVersionNotificationStatus.SENT);
				artifactVersionNotificationService.update(notification);
			}
			
			notificationsSent += entry.getValue().size();
		}
		statisticService.feed(StatisticEnumKey.NOTIFICATIONS_SENT_PER_DAY, notificationsSent);
	}
	
	@Override
	public void synchronizeAllArtifactsAndNotifyUsers() throws ServiceException, SecurityServiceException, InterruptedException {
		synchronizeArtifactsAndNotifyUsers(artifactService.listIds());
		propertyService.set(MavenArtifactNotifierCorePropertyIds.LAST_SYNCHRONIZATION_DATE, new Date());
	}
	
	@Override
	public void refreshAllLatestVersions() throws ServiceException, SecurityServiceException {
		for (Artifact artifact : artifactService.list()) {
			artifact.refreshCachedVersions();
			artifactService.update(artifact);
		}
	}
	
	private void synchronizeArtifacts(List<Long> artifactIds) throws ServiceException, SecurityServiceException, InterruptedException {
		Integer pauseDelayInMilliseconds = configurer.getSynchronizationPauseDelayBetweenRequestsInMilliseconds();
		
		MutableInt versionsReleasedCount = new MutableInt(0);
		
		List<List<Long>> artifactIdsPartitions = Lists.partition(artifactIds, BATCH_PARTITION_SIZE);
		
		for (List<Long> artifactIdsPartition : artifactIdsPartitions) {
			for (Long artifactId : artifactIdsPartition) {
				Artifact artifact = artifactService.getById(artifactId);
				synchronizeArtifact(artifact, versionsReleasedCount);
				
				if (pauseDelayInMilliseconds != null) {
					Thread.sleep(pauseDelayInMilliseconds);
				}
			}
			artifactService.flush();
			artifactService.clear();
		}
		statisticService.feed(StatisticEnumKey.VERSIONS_RELEASED_PER_DAY, versionsReleasedCount.toInteger());
	}
	
	private void synchronizeArtifact(Artifact artifact, MutableInt versionsReleasedCount) throws ServiceException, SecurityServiceException {
		List<ArtifactVersionBean> versions = artifactVersionProviderService.getArtifactVersions(artifact.getGroup().getGroupId(),
				artifact.getArtifactId());
		
		int newVersionsCount = addNewVersions(artifact, versions);
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
				createNotificationsForUsers(artifact);
			}
		}
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
	
	private void createNotificationsForUsers(Artifact artifact) throws ServiceException, SecurityServiceException {
		List<FollowedArtifact> followedArtifacts = followedArtifactService.listByArtifact(artifact);
		
		for (FollowedArtifact followedArtifact : followedArtifacts) {
			followedArtifactService.update(followedArtifact);
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
		
		List<ArtifactVersion> lastVersions = artifactService.listArtifactVersionsAfterDate
				(followedArtifact.getArtifact(), followedArtifact.getLastNotifiedVersionDate());
		
		List<ArtifactNotificationRule> rules = followedArtifactService.listArtifactNotificationRules(followedArtifact);
		
		List<ArtifactVersionNotification> notifications = Lists.newArrayList();
		for (ArtifactVersion version : lastVersions) {
			// Rules check
			if (!artifactNotificationRuleService.checkRulesForVersion(version.getVersion(), rules)) {
				continue;
			}
			
			ArtifactVersionNotification notification = new ArtifactVersionNotification(version);
			artifactVersionNotificationService.create(notification);
			
			follower.addNotification(notification);
			notifications.add(notification);
			if (followedArtifact.getLastNotifiedVersionDate().before(version.getLastUpdateDate())) {
				followedArtifact.setLastNotifiedVersionDate(version.getLastUpdateDate());
			}
		}
		if (!lastVersions.isEmpty()) {
			userService.update(follower);
		}
	}

}
