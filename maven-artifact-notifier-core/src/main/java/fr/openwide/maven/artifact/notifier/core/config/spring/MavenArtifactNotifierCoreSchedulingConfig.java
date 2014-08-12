package fr.openwide.maven.artifact.notifier.core.config.spring;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.more.business.link.service.IExternalLinkCheckerService;
import fr.openwide.maven.artifact.notifier.core.business.sync.service.IMavenSynchronizationService;

@EnableScheduling
@Configuration
public class MavenArtifactNotifierCoreSchedulingConfig {
	
	@Autowired
	private IMavenSynchronizationService mavenSynchronizationService;
	
	@Autowired
	private IExternalLinkCheckerService linkCheckerService;
	
	@Scheduled(cron = "${scheduler.synchronizeAllArtifactsAndNotifyUsers.cron}")
	public void synchronizeAllArtifacts() throws ServiceException, SecurityServiceException, InterruptedException {
		mavenSynchronizationService.synchronizeAllArtifactsAndNotifyUsers();
	}
	
	@Scheduled(cron = "${scheduler.initializeAllArtifacts.cron}")
	public void initializeAllArtifacts() throws ServiceException, SecurityServiceException, InterruptedException {
		mavenSynchronizationService.initializeAllArtifacts();
	}
	
	@Scheduled(cron = "${scheduler.checkAllLinks.cron}")
	public void checkAllLinks() throws ServiceException, SecurityServiceException, URISyntaxException {
		linkCheckerService.checkBatch();
	}
}
