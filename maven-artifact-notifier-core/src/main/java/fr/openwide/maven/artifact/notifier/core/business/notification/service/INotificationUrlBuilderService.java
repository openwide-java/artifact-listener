package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;


public interface INotificationUrlBuilderService {
	
	String getHomeUrl();
	
	String getAboutUrl();
	
	String getGitHubUrl();
	
	String getProfileUrl();
	
	String getConfirmRegistrationUrl(User user);
	
	String getResetPasswordUrl(User user);
	
	String getConfirmEmailUrl(EmailAddress emailAddress);
	
	String getDeleteEmailUrl(EmailAddress emailAddress);
	
	String getArtifactDescriptionUrl(Artifact artifact);
}
