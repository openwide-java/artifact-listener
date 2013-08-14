package fr.openwide.maven.artifact.notifier.core.util.binding;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroupBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotificationBinding;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifactBinding;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ItemAdditionalInformationBinding;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectBinding;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectLicenseBinding;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersionBinding;
import fr.openwide.maven.artifact.notifier.core.business.project.model.VersionAdditionalInformationBinding;
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
	
	private static final ProjectBinding PROJECT = new ProjectBinding();
	
	private static final ProjectVersionBinding PROJECT_VERSION = new ProjectVersionBinding();
	
	private static final ProjectLicenseBinding PROJECT_LICENSE = new ProjectLicenseBinding();
	
	private static final ItemAdditionalInformationBinding ITEM_ADDITIONAL_INFORMATION = new ItemAdditionalInformationBinding();
	
	private static final VersionAdditionalInformationBinding VERSION_ADDITIONAL_INFORMATION = new VersionAdditionalInformationBinding();

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
	
	public static ProjectBinding project() {
		return PROJECT;
	}
	
	public static ProjectVersionBinding projectVersion() {
		return PROJECT_VERSION;
	}
	
	public static ProjectLicenseBinding projectLicense() {
		return PROJECT_LICENSE;
	}
	
	public static ItemAdditionalInformationBinding itemAdditionalInformation() {
		return ITEM_ADDITIONAL_INFORMATION;
	}
	
	public static VersionAdditionalInformationBinding versionAdditionalInformation() {
		return VERSION_ADDITIONAL_INFORMATION;
	}
	
	private Binding() {
	}
}
