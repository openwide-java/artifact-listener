package fr.openwide.maven.artifact.notifier.core.business.user.service;

import java.util.Collection;
import java.util.List;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.security.business.person.service.IGenericUserService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.user.model.AuthenticationType;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;

public interface IUserService extends IGenericUserService<User> {
	
	User getAuthenticatedUser();
	
	List<User> search(String searchPattern, int limit, int offset) throws ServiceException;
	
	long countSearch(String searchPattern) throws ServiceException;

	List<User> listByUserName(String userName);

	FollowedArtifact followArtifact(User user, Artifact artifact) throws ServiceException, SecurityServiceException;
	
	FollowedArtifact followArtifactBean(User user, ArtifactBean artifactBean) throws ServiceException, SecurityServiceException;
	
	void followProject(User user, Project project) throws ServiceException, SecurityServiceException;
	
	boolean unfollowArtifact(User user, Artifact artifact) throws ServiceException, SecurityServiceException;

	boolean unfollowArtifact(User user, FollowedArtifact followedArtifact) throws ServiceException, SecurityServiceException;
	
	void unfollowProject(User user, Project project) throws ServiceException, SecurityServiceException;

	List<ArtifactVersionNotification> listRecentNotifications(User user);
	
	Collection<FollowedArtifact> listFollowedArtifacts(User user);
	
	Collection<EmailAddress> listAdditionalEmails(User user);
	
	Collection<ArtifactKey> listFollowedArtifactKeys(User user);
	
	FollowedArtifact getFollowedArtifact(User user, Artifact artifact);
	
	boolean isFollowedArtifact(User user, Artifact artifact);
	
	boolean isFollowedArtifactBean(User user, ArtifactBean artifactBean);
	
	boolean isFollowedProject(User user, Project project);
	
	void register(User user, AuthenticationType authenticationType, String password) throws ServiceException, SecurityServiceException;

	void confirmRegistration(User user) throws ServiceException, SecurityServiceException;
	
	void passwordResetRequest(User user) throws ServiceException, SecurityServiceException;
	
	void changePassword(User user, String newPassword) throws ServiceException, SecurityServiceException;

	User getByNotificationHash(String hash);
	
	boolean addEmailAddress(User user, String email) throws ServiceException, SecurityServiceException;
	
	void doDeleteEmailAddress(EmailAddress emailAddress) throws ServiceException, SecurityServiceException;
	
	void deleteEmailAddress(EmailAddress emailAddress) throws ServiceException, SecurityServiceException;
	
	User getByRemoteIdentifier(String remoteIdentifier);
	
	String getHash(User user, String key);

	List<User> listByUserGroup(UserGroup userGroup);
}