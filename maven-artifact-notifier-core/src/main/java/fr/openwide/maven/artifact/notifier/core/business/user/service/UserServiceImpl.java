package fr.openwide.maven.artifact.notifier.core.business.user.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mysema.query.jpa.impl.JPAQuery;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.search.service.IHibernateSearchService;
import fr.openwide.core.jpa.security.business.person.service.GenericSimpleUserServiceImpl;
import fr.openwide.core.jpa.security.service.IAuthenticationService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactGroupService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationService;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.user.dao.IUserDao;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyAddedEmailException;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.model.AuthenticationType;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailStatus;
import fr.openwide.maven.artifact.notifier.core.business.user.model.QUser;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User_;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

@Service("personService")
public class UserServiceImpl extends GenericSimpleUserServiceImpl<User> implements IUserService {

	@Autowired
	private IHibernateSearchService hibernateSearchService;
	
	@Autowired
	private IArtifactService artifactService;
	
	@Autowired
	private IArtifactGroupService artifactGroupService;
	
	@Autowired
	private IFollowedArtifactService followedArtifactService;
	
	@Autowired
	private INotificationService notificationService;
	
	@Autowired
	private IEmailAddressService emailAddressService;
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Autowired
	private IAuthenticationService authenticationService;
	
	private IUserDao userDao;
	
	@Autowired
	public UserServiceImpl(IUserDao userDao) {
		super(userDao);
		this.userDao = userDao;
	}
	
	@Override
	public User getAuthenticatedUser() {
		String userName = authenticationService.getUserName();
		if (userName == null) {
			return null;
		}
		
		return getByUserName(userName);
	}

	@Override
	public List<User> listByUserName(String userName) {
		return listByField(User_.userName, userName);
	}
	
	@Override
	public List<User> search(String searchPattern, int limit, int offset) throws ServiceException {
		return userDao.search(searchPattern, limit, offset);
	}
	
	@Override
	public long countSearch(String searchPattern) throws ServiceException {
		return userDao.countSearch(searchPattern);
	}
	
	@Override
	public FollowedArtifact followArtifact(User user, Artifact artifact) throws ServiceException, SecurityServiceException {
		FollowedArtifact followedArtifact = getFollowedArtifact(user, artifact);
		if (followedArtifact != null) {
			throw new AlreadyFollowedArtifactException();
		} else {
			followedArtifact = new FollowedArtifact(artifact);
			followedArtifactService.create(followedArtifact);
			
			artifact.setFollowersCount(artifact.getFollowersCount() + 1);
			
			user.addFollowedArtifact(followedArtifact);
			update(user);
		}
		return followedArtifact;
	}
	
	@Override
	public FollowedArtifact followArtifactBean(User user, ArtifactBean artifactBean) throws ServiceException, SecurityServiceException {
		Artifact artifact = artifactService.getOrCreate(artifactBean.getArtifactKey());
		
		return followArtifact(user, artifact);
	}
	
	@Override
	public void followProject(User user, Project project) throws ServiceException, SecurityServiceException {
		for (Artifact artifact : project.getArtifacts()) {
			try {
				followArtifact(user, artifact);
			} catch (AlreadyFollowedArtifactException e) {
				continue;
			}
		}
	}
	
	@Override
	public boolean unfollowArtifact(User user, FollowedArtifact followedArtifact) throws ServiceException, SecurityServiceException {
		Artifact artifact = followedArtifact.getArtifact();
		artifact.setFollowersCount(Math.max(0, artifact.getFollowersCount() - 1));
		
		user.getFollowedArtifacts().remove(followedArtifact);
		update(user);
		
		followedArtifactService.delete(followedArtifact);
		return true;
	}
	
	@Override
	public boolean unfollowArtifact(User user, Artifact artifact) throws ServiceException, SecurityServiceException {
		FollowedArtifact followedArtifact = getFollowedArtifact(user, artifact);
		if (followedArtifact == null) {
			return false;
		} else {
			return unfollowArtifact(user, followedArtifact);
		}
	}
	
	@Override
	public void unfollowProject(User user, Project project) throws ServiceException, SecurityServiceException {
		for (Artifact artifact : project.getArtifacts()) {
			unfollowArtifact(user, artifact);
		}
	}
	
	@Override
	public List<ArtifactVersionNotification> listRecentNotifications(User user) {
		int limit = configurer.getLastNotificationsLimit();
		int dayCount = configurer.getLastNotificationsDayCount();
		
		List<ArtifactVersionNotification> notificationList = userDao.listLastNotifications(user, limit);
		
		ArtifactVersionNotification oldestNotification = Iterables.getLast(notificationList, null);
		
		if (oldestNotification != null) {
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -dayCount);
			
			if (oldestNotification.getCreationDate().after(calendar.getTime())) {
				notificationList = userDao.listNotificationsAfterDate(user, calendar.getTime());
			}
		}
		return notificationList;
	}
	
	@Override
	public Collection<FollowedArtifact> listFollowedArtifacts(User user) {
		return user.getFollowedArtifacts();
	}
	
	@Override
	public Collection<EmailAddress> listAdditionalEmails(User user) {
		return user.getAdditionalEmails();
	}

	@Override
	public Collection<ArtifactKey> listFollowedArtifactKeys(User user) {
		Set<ArtifactKey> artifactKeyList = Sets.newTreeSet();
		Collection<FollowedArtifact> followedArtifactList = listFollowedArtifacts(user);
		
		for (FollowedArtifact followedArtifact : followedArtifactList) {
			artifactKeyList.add(followedArtifact.getArtifact().getArtifactKey());
		}
		return artifactKeyList;
	}
	
	@Override
	public FollowedArtifact getFollowedArtifact(User user, Artifact artifact) {
		return userDao.getFollowedArtifact(user, artifact);
	}
	
	@Override
	public boolean isFollowedArtifact(User user, Artifact artifact) {
		return artifact != null && getFollowedArtifact(user, artifact) != null;
	}
	
	@Override
	public boolean isFollowedArtifactBean(User user, ArtifactBean artifactBean) {
		return isFollowedArtifact(user, artifactService.getByArtifactKey(artifactBean.getArtifactKey()));
	}
	
	@Override
	public boolean isFollowedProject(User user, Project project) {
		boolean result = !project.getArtifacts().isEmpty();
		for (Artifact artifact : project.getArtifacts()) {
			result = result && isFollowedArtifact(user, artifact);
		}
		return result;
	}
	
	@Override
	public void register(User user, AuthenticationType authenticationType, String password) throws ServiceException, SecurityServiceException {
		user.setAuthenticationType(authenticationType);
		user.setNotificationHash(getHash(user, user.getUserName()));
		create(user);
		if (password != null) {
			setPasswords(user, password);
		}
		notificationService.sendConfirmRegistrationNotification(user);
	}
	
	@Override
	public void confirmRegistration(User user) throws ServiceException, SecurityServiceException {
		user.setNotificationHash(null);
		
		if (AuthenticationType.OAUTH2_GOOGLE.equals(user.getAuthenticationType())) {
			User oldProfile = getOldGoogleOpenIdProfile(user.getEmail());
			if (oldProfile != null) {
				oldProfile.copyProfileToUser(user);
				oldProfile.setActive(false);
			}
		}
	}
	
	@Override
	public void passwordResetRequest(User user) throws ServiceException, SecurityServiceException {
		Date now = new Date();
		user.setNotificationHash(getHash(user, user.getPasswordHash() + now));
		update(user);
		
		notificationService.sendResetPasswordNotification(user);
	}
	
	@Override
	public void changePassword(User user, String newPassword) throws ServiceException, SecurityServiceException {
		user.setNotificationHash(null);
		setPasswords(user, newPassword);
	}
	
	@Override
	public User getByNotificationHash(String hash) {
		return getByField(User_.notificationHash, hash);
	}
	
	@Override
	public boolean addEmailAddress(User user, String email) throws ServiceException, SecurityServiceException {
		for (EmailAddress emailAddress : user.getAdditionalEmails()) {
			if (emailAddress.getEmail().compareToIgnoreCase(email) == 0) {
				throw new AlreadyAddedEmailException();
			}
		}
		
		String hash = getHash(user, email);

		if (emailAddressService.getByHash(hash) == null) {
			EmailAddress emailAddress = new EmailAddress(email);
			emailAddress.setEmailHash(hash);
			user.addAdditionalEmail(emailAddress);
			update(user);
			
			notificationService.sendConfirmEmailNotification(emailAddress);
			return true;
		}
		return false;
	}
	
	@Override
	public void doDeleteEmailAddress(EmailAddress emailAddress) throws ServiceException, SecurityServiceException {
		emailAddress.getUser().getAdditionalEmails().remove(emailAddress);
		update(emailAddress.getUser());

		emailAddressService.delete(emailAddress);
	}
	
	@Override
	public void deleteEmailAddress(EmailAddress emailAddress) throws ServiceException, SecurityServiceException {
		emailAddressService.changeStatus(emailAddress, EmailStatus.PENDING_DELETE);
		notificationService.sendDeleteEmailNotification(emailAddress);
	}
	
	@Override
	public User getByRemoteIdentifier(String remoteIdentifier) {
		List<User> userList = listByField(User_.remoteIdentifier, remoteIdentifier);
		return userList.size() > 0 ? userList.get(0) : null;
	}
	
	@Override
	public List<User> listByUserGroup(UserGroup userGroup) {
		return userDao.listByUserGroup(userGroup);
	}
	
	@Override
	public User getOldGoogleOpenIdProfile(String email) {
		return userDao.getOldGoogleOpenIdProfile(email);
	}
	
	@Override
	public String getHash(User user, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(RandomStringUtils.randomAscii(8))
			.append(user.getId())
			.append(key)
			.append(user.getCreationDate());
		return DigestUtils.sha1Hex(sb.toString());
	}
}
