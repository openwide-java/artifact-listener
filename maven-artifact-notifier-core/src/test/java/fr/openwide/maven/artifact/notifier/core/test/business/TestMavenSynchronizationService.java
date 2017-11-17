package fr.openwide.maven.artifact.notifier.core.test.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactGroupService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic;
import fr.openwide.maven.artifact.notifier.core.business.statistics.service.IStatisticService;
import fr.openwide.maven.artifact.notifier.core.business.sync.service.IMavenSynchronizationService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.test.AbstractMavenArtifactNotifierTestCase;

public class TestMavenSynchronizationService extends AbstractMavenArtifactNotifierTestCase {

	@Autowired
	private IArtifactGroupService artifactGroupService;

	@Autowired
	private IArtifactService artifactService;

	@Autowired
	private IStatisticService statisticService;

	@Autowired
	private IArtifactVersionService artifactVersionService;

	@Autowired
	private IMavenSynchronizationService mavenSynchronizationService;

	@Ignore("Not ready : conf smtp out of order")
	@Test
	public void testNotifications() throws ServiceException, SecurityServiceException, InterruptedException {

		{
			ArtifactGroup group = new ArtifactGroup("org.apache.commons");
			artifactGroupService.create(group);

			Artifact artifact = new Artifact("commons-exec");
			group.addArtifact(artifact);
			artifactService.create(artifact);

			User user = new User();
			user.setUserName("firstname.lastname@test.fr");
			user.setEmail(user.getUserName());
			userService.create(user);
			
			Calendar previousDate = GregorianCalendar.getInstance();
			previousDate.set(GregorianCalendar.DAY_OF_MONTH, -1);
			
			userService.followArtifact(user, artifact);
			Iterables.get(user.getFollowedArtifacts(), 0).setLastNotifiedVersionDate(previousDate.getTime());
			artifact.setStatus(ArtifactStatus.INITIALIZED);
			
			ArtifactVersion artifactVersion = new ArtifactVersion("2.0-test", new Date());
			artifactVersionService.create(artifactVersion);
			artifact.addVersion(artifactVersion);
		}
		mavenSynchronizationService.synchronizeAllArtifactsAndNotifyUsers();

		User testUser = userService.getByUserName("firstname.lastname@test.fr");
		assertTrue(!testUser.getFollowedArtifacts().isEmpty());
		assertNotNull(Iterables.get(testUser.getFollowedArtifacts(), 0).getLastNotifiedVersionDate());

		assertTrue(!testUser.getNotifications().isEmpty());
		assertEquals("2.0-test", Iterables.get(testUser.getNotifications(), 0).getArtifactVersion().getVersion());
	}

	@Override
	protected void cleanAll() throws ServiceException, SecurityServiceException {
		super.cleanAll();
		cleanArtifactGroups();
		cleanStatistics();
	}

	protected void cleanArtifactGroups() throws ServiceException, SecurityServiceException {
		for (ArtifactGroup artifactGroup : artifactGroupService.list()) {
			artifactGroupService.delete(artifactGroup);
		}
	}
	
	protected void cleanStatistics() throws ServiceException, SecurityServiceException {
		for (Statistic statistic : statisticService.list()) {
			statisticService.delete(statistic);
		}
	}
}
