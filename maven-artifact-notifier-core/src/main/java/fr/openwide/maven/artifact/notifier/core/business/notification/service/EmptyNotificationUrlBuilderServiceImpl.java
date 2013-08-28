package fr.openwide.maven.artifact.notifier.core.business.notification.service;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

/**
 * Implémentation bouche-trou, uniquement pour combler la dépendance.
 */
public class EmptyNotificationUrlBuilderServiceImpl implements INotificationUrlBuilderService {

	@Override
	public String getHomeUrl() {
		return "";
	}
	
	@Override
	public String getAboutUrl() {
		return "";
	}
	
	@Override
	public String getGitHubUrl() {
		return "";
	}
	
	@Override
	public String getProfileUrl() {
		return "";
	}
	
	@Override
	public String getConfirmRegistrationUrl(User user) {
		return "";
	}
	
	@Override
	public String getResetPasswordUrl(User user) {
		return "";
	}
	
	@Override
	public String getConfirmEmailUrl(EmailAddress emailAddress) {
		return "";
	}

	@Override
	public String getDeleteEmailUrl(EmailAddress emailAddress) {
		return "";
	}
	
	@Override
	public String getArtifactDescriptionUrl(Artifact artifact) {
		return "";
	}
}
