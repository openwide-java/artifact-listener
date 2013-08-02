package fr.openwide.maven.artifact.notifier.core.test.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactGroupService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.sync.service.IMavenSynchronizationService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.test.AbstractMavenArtifactNotifierTestCase;

public class TestArtifactService extends AbstractMavenArtifactNotifierTestCase {

	@Autowired
	private IArtifactGroupService artifactGroupService;
	
	@Autowired
	private IArtifactService artifactService;
	
	@Autowired
	private IFollowedArtifactService followedArtifactService;
	
	@Autowired
	private IMavenSynchronizationService mavenSynchronizationService;
	
	@Test
	public void testSynchronizeVersions() throws ServiceException, SecurityServiceException, InterruptedException {
		
		{
			ArtifactGroup group = new ArtifactGroup("com.google.inject");
			artifactGroupService.create(group);
			
			Artifact artifact = new Artifact("guice");
			group.addArtifact(artifact);
			artifactService.create(artifact);
			
			User user = new User();
			user.setUserName("firstname.lastname@test.fr");
			user.setEmail(user.getUserName());
			userService.create(user);

			userService.followArtifact(user, artifact);
		}
		
		List<Artifact> artifactList = artifactService.list();
		
		assertEquals(1, artifactList.size());
		Artifact artifact = artifactList.get(0);
		assertEquals("com.google.inject", artifact.getGroup().getGroupId());
		assertEquals("guice", artifact.getArtifactId());
		assertEquals(ArtifactStatus.NOT_INITIALIZED, artifact.getStatus());
		
		mavenSynchronizationService.synchronizeAllArtifactsAndNotifyUsers();
		assertEquals(ArtifactStatus.INITIALIZED, artifact.getStatus());
		assertTrue(artifact.getVersions().size() > 0);
		ArtifactVersion firstVersion = (ArtifactVersion) artifact.getVersions().toArray()[artifact.getVersions().size() - 1];
		assertEquals("1.0", firstVersion.getVersion());
	}
	
	@Override
	protected void cleanAll() throws ServiceException, SecurityServiceException {
		super.cleanAll();
		cleanArtifactGroups();
	}
	
	protected void cleanArtifactGroups() throws ServiceException, SecurityServiceException {
		for (ArtifactGroup artifactGroup : artifactGroupService.list()) {
			artifactGroupService.delete(artifactGroup);
		}
	}
}
