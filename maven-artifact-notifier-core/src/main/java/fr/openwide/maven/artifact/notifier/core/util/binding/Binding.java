package fr.openwide.maven.artifact.notifier.core.util.binding;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroupBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotificationBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifactBinding;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBeanBinding;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBeanBinding;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddressBinding;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserBinding;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroupBinding;

public final class Binding {

	private static final UserBinding USER = new UserBinding();

	private static final UserGroupBinding USER_GROUP = new UserGroupBinding();
	
	private static final ArtifactGroupBinding ARTIFACT_GROUP = new ArtifactGroupBinding();
	
	private static final ArtifactBinding ARTIFACT = new ArtifactBinding();
	
	private static final ArtifactVersionBinding ARTIFACT_VERSION = new ArtifactVersionBinding();
	
	private static final ArtifactVersionNotificationBinding NOTIFICATION = new ArtifactVersionNotificationBinding();
	
	private static final FollowedArtifactBinding FOLLOWED_ARTIFACT = new FollowedArtifactBinding();
	
	private static final EmailAddressBinding EMAIL_ADDRESS = new EmailAddressBinding();
	
	private static final ArtifactNotificationRuleBinding ARTIFACT_NOTIFICATION_RULE = new ArtifactNotificationRuleBinding();
	
	private static final PomBeanBinding POM_BEAN = new PomBeanBinding();
	
	private static final ArtifactBeanBinding ARTIFACT_BEAN = new ArtifactBeanBinding();

	public static UserBinding user() {
		return USER;
	}

	public static UserGroupBinding userGroup() {
		return USER_GROUP;
	}
	
	public static ArtifactGroupBinding artifactGroup() {
		return ARTIFACT_GROUP;
	}
	
	public static ArtifactBinding artifact() {
		return ARTIFACT;
	}
	
	public static ArtifactVersionBinding artifactVersion() {
		return ARTIFACT_VERSION;
	}
	
	public static ArtifactVersionNotificationBinding artifactVersionNotification() {
		return NOTIFICATION;
	}
	
	public static FollowedArtifactBinding followedArtifact() {
		return FOLLOWED_ARTIFACT;
	}
	
	public static EmailAddressBinding emailAddress() {
		return EMAIL_ADDRESS;
	}
	
	public static ArtifactNotificationRuleBinding artifactNotificationRule() {
		return ARTIFACT_NOTIFICATION_RULE;
	}
	
	public static PomBeanBinding pomBean() {
		return POM_BEAN;
	}
	
	public static ArtifactBeanBinding artifactBean() {
		return ARTIFACT_BEAN;
	}
	
	private Binding() {
	}
}
