package fr.openwide.maven.artifact.notifier.core.business.sync.service;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.spring.property.service.IPropertyService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IBatchArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic.StatisticEnumKey;
import fr.openwide.maven.artifact.notifier.core.business.statistics.service.IStatisticService;
import fr.openwide.maven.artifact.notifier.core.property.MavenArtifactNotifierCorePropertyIds;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("mavenSynchronizationService")
public class MavenSynchronizationServiceImpl implements IMavenSynchronizationService {

	private static final Logger LOG = LoggerFactory.getLogger(MavenSynchronizationServiceImpl.class);

	@Autowired
	private IArtifactService artifactService;

	@Autowired
	private IStatisticService statisticService;

	@Autowired
	private IPropertyService propertyService;

	@Autowired
	private IBatchArtifactService batchArtifactService;

	@Override
	public void initializeAllArtifacts() throws ServiceException, SecurityServiceException {
		synchronizeArtifacts(artifactService.listIdsByStatus(ArtifactStatus.NOT_INITIALIZED));
	}

	@Override
	public void synchronizeArtifactsAndNotifyUsers(List<Long> artifactIds) throws ServiceException, SecurityServiceException {
		synchronizeArtifacts(artifactIds);
		batchArtifactService.notifyUsers();
	}

	@Override
	public void synchronizeAllArtifactsAndNotifyUsers() throws ServiceException, SecurityServiceException {
		synchronizeArtifactsAndNotifyUsers(artifactService.listIds());

		Date now = new Date();
		LOG.info("Updating {} to {}", MavenArtifactNotifierCorePropertyIds.LAST_SYNCHRONIZATION_DATE, now);
		propertyService.set(MavenArtifactNotifierCorePropertyIds.LAST_SYNCHRONIZATION_DATE, now);
	}

	@Override
	public void refreshAllLatestVersions() throws ServiceException, SecurityServiceException {
		for (Artifact artifact : artifactService.list()) {
			artifact.refreshCachedVersions();
			artifactService.update(artifact);
		}
	}

	private void synchronizeArtifacts(List<Long> artifactIds) throws ServiceException, SecurityServiceException {
		if (artifactIds == null || artifactIds.isEmpty()) {
			LOG.warn("No artifact to synchronize");
			return;
		}

		LOG.info("Synchronizing {} artifacts with user notification: {}", artifactIds.size(), artifactIds);
		Instant start = Instant.now();

		MutableInt versionsReleasedCount = new MutableInt(0);

		LOG.info("{} artifacts to synchronize", artifactIds.size());

		int i = 1;
		for (Long artifactId : artifactIds) {
			LOG.info("Artifact #{} of {} ({}%)", i, artifactIds.size(), i * 100 / artifactIds.size());
			i++;

			try {
				batchArtifactService.synchronizeArtifact(artifactId, versionsReleasedCount);
			} catch (Exception e) {
				LOG.error(String.format("Error fetching data for %1$s. Artifact ignored.", artifactId), e);
			}
		}
		statisticService.feed(StatisticEnumKey.VERSIONS_RELEASED_PER_DAY, versionsReleasedCount.toInteger());

		LOG.info("Finished synchronizing artifacts in {}ms", new Duration(start, Instant.now()).getMillis());
	}

}
