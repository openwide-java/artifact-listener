package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactNotificationRuleService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionNotificationService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationService;
import fr.openwide.maven.artifact.notifier.core.business.parameter.service.IParameterService;
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

@Service("mavenSynchronizationService")
public class MavenSynchronizationServiceImpl implements IMavenSynchronizationService {

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
	private IParameterService parameterService;
	
	@Autowired
	private IStatisticService statisticService;
	
	@Autowired
	private IProjectService projectService;
	
	@Autowired
	private IProjectVersionService projectVersionService;
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Override
	public void initializeAllArtifacts() throws ServiceException, SecurityServiceException, InterruptedException {
		synchronizeArtifacts(artifactService.listByStatus(ArtifactStatus.NOT_INITIALIZED));
	}
	
	@Override
	public void synchronizeArtifactsAndNotifyUsers(List<Artifact> artifacts) throws ServiceException, SecurityServiceException, InterruptedException {
		Map<User, List<ArtifactVersionNotification>> notificationsByUser = synchronizeArtifacts(artifacts);
		
		int notificationsSent = 0;
		for (Map.Entry<User, List<ArtifactVersionNotification>> entry : notificationsByUser.entrySet()) {
			Collections.sort(entry.getValue());
			notificationService.sendNewVersionNotification(entry.getValue(), entry.getKey());
			
			notificationsSent += entry.getValue().size();
		}
		statisticService.feed(StatisticEnumKey.NOTIFICATIONS_SENT_PER_DAY, notificationsSent);
	}
	
	@Override
	public void synchronizeAllArtifactsAndNotifyUsers() throws ServiceException, SecurityServiceException, InterruptedException {
		synchronizeArtifactsAndNotifyUsers(artifactService.list());
		parameterService.setLastSynchronizationDate(new Date());
	}
	
	@Override
	public void refreshAllLatestVersions() throws ServiceException, SecurityServiceException {
		for (Artifact artifact : artifactService.list()) {
			artifact.refreshCachedVersions();
			artifactService.update(artifact);
		}
	}
	
	private Map<User, List<ArtifactVersionNotification>> synchronizeArtifacts(List<Artifact> artifacts) throws ServiceException, SecurityServiceException, InterruptedException {
		Integer pauseDelayInMilliseconds = configurer.getSynchronizationPauseDelayBetweenRequestsInMilliseconds();
		
		MutableInt versionsReleasedCount = new MutableInt(0);
		Map<User, List<ArtifactVersionNotification>> notificationsByUser = Maps.newHashMap();
		for (Artifact artifact : artifacts) {
			Map<User, List<ArtifactVersionNotification>> artifactNotificationsByUser = synchronizeArtifact(artifact, versionsReleasedCount);
			
			for (Map.Entry<User, List<ArtifactVersionNotification>> entry : artifactNotificationsByUser.entrySet()) {
				if (notificationsByUser.containsKey(entry.getKey())) {
					notificationsByUser.get(entry.getKey()).addAll(entry.getValue());
				} else {
					notificationsByUser.put(entry.getKey(), entry.getValue());
				}
			}
			if (pauseDelayInMilliseconds != null) {
				Thread.sleep(pauseDelayInMilliseconds);
			}
		}
		statisticService.feed(StatisticEnumKey.VERSIONS_RELEASED_PER_DAY, versionsReleasedCount.toInteger());
		
		return notificationsByUser;
	}
	
	private Map<User, List<ArtifactVersionNotification>> synchronizeArtifact(Artifact artifact, MutableInt versionsReleasedCount) throws ServiceException, SecurityServiceException {
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
				return createNotificationsForUsers(artifact);
			}
		}
		
		return Maps.newHashMapWithExpectedSize(0);
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
			} else {
				if (ProjectVersionStatus.IN_PROGRESS.equals(projectVersion.getStatus())) {
					projectVersion.setStatus(ProjectVersionStatus.PUBLISHED_ON_MAVEN_CENTRAL);
				}
				projectVersion.setLastUpdateDate(new Date());
				projectVersionService.update(projectVersion);
			}
			
			artifactVersion.setProjectVersion(projectVersion);
			artifactVersionService.update(artifactVersion);
		}
	}
	
	private Map<User, List<ArtifactVersionNotification>> createNotificationsForUsers(Artifact artifact) throws ServiceException, SecurityServiceException {
		Map<User, List<ArtifactVersionNotification>> notificationsByUser = Maps.newHashMap();
		List<FollowedArtifact> followedArtifacts = followedArtifactService.listByArtifact(artifact);
		
		for (FollowedArtifact followedArtifact : followedArtifacts) {
			followedArtifactService.update(followedArtifact);
			List<ArtifactVersionNotification> notifications = createNotifications(followedArtifact);
			if (!notifications.isEmpty()) {
				notificationsByUser.put(followedArtifact.getUser(), notifications);
			}
		}
		return notificationsByUser;
	}
	
	private List<ArtifactVersionNotification> createNotifications(FollowedArtifact followedArtifact) throws ServiceException, SecurityServiceException {
		if (followedArtifact.getLastNotifiedVersionDate() == null) {
			// this case shouldn't happen but it's better to be on the safe side rather than sending a bunch of unwanted notifications...
			followedArtifact.setLastNotifiedVersionDate(new Date());
			followedArtifactService.update(followedArtifact);
			return Lists.newArrayListWithCapacity(0);
		}
		
		User follower = followedArtifact.getUser();
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
		return notifications;
	}

}
