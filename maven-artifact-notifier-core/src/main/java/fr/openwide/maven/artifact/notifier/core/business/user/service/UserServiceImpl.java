package fr.openwide.maven.artifact.notifier.core.business.user.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.search.service.IHibernateSearchService;
import fr.openwide.core.jpa.security.business.person.service.AbstractPersonServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactGroup;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersionNotification;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactGroupService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.notification.service.INotificationService;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.user.dao.IUserDao;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailStatus;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User_;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

@Service("personService")
public class UserServiceImpl extends AbstractPersonServiceImpl<User> implements IUserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
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
	
	private IUserDao userDao;
	
	@Autowired
	public UserServiceImpl(IUserDao userDao) {
		super(userDao);
		this.userDao = userDao;
	} 

	@Override
	public List<User> listByUserName(String userName) {
		return listByField(User_.userName, userName);
	}
	
	@Override
	public List<User> search(String searchPattern) throws ServiceException, SecurityServiceException {
		String[] searchFields = new String[] { Binding.user().userName().getPath(), Binding.user().fullName().getPath() };
		
		return hibernateSearchService.search(getObjectClass(), searchFields, searchPattern);
	}
	
	@Override
	public List<User> searchAutocomplete(String searchPattern) throws ServiceException, SecurityServiceException {
		String[] searchFields = new String[] { Binding.user().userName().getPath(), Binding.user().fullName().getPath() };
		
		return hibernateSearchService.searchAutocomplete(getObjectClass(), searchFields, searchPattern);
	}
	
	@Override
	public FollowedArtifact followArtifact(User user, Artifact artifact) throws ServiceException, SecurityServiceException {
		FollowedArtifact followedArtifact = getFollowedArtifact(user, artifact);
		if (followedArtifact != null) {
			LOGGER.info("Artifact " + artifact.getDisplayName() + " is already followed by user " + user.getDisplayName() + ".");
			throw new AlreadyFollowedArtifactException();
		} else {
			followedArtifact = new FollowedArtifact(artifact);
			followedArtifactService.create(followedArtifact);
			
			user.addFollowedArtifact(followedArtifact);
			update(user);
		}
		return followedArtifact;
	}
	
	@Override
	public FollowedArtifact followArtifactBean(User user, ArtifactBean artifactBean) throws ServiceException, SecurityServiceException {
		ArtifactGroup artifactGroup = artifactGroupService.getByGroupId(artifactBean.getGroupId());
		if (artifactGroup == null) {
			artifactGroup = new ArtifactGroup(artifactBean.getGroupId());
			artifactGroupService.create(artifactGroup);
		}
		
		Artifact artifact = artifactService.getByArtifactKey(artifactBean.getArtifactKey());
		if (artifact == null) {
			artifact = new Artifact(artifactBean.getArtifactId());
			artifactGroup.addArtifact(artifact);
			artifactService.create(artifact);
		}
		
		return followArtifact(user, artifact);
	}
	
	@Override
	public boolean unfollowArtifact(User user, FollowedArtifact followedArtifact) throws ServiceException, SecurityServiceException {
		user.getFollowedArtifacts().remove(followedArtifact);
		update(user);
		
		followedArtifactService.delete(followedArtifact);
		return true;
	}
	
	@Override
	public boolean unfollowArtifact(User user, Artifact artifact) throws ServiceException, SecurityServiceException {
		FollowedArtifact followedArtifact = getFollowedArtifact(user, artifact);
		if (followedArtifact == null) {
			LOGGER.info("Artifact " + artifact.getDisplayName() + " is not followed by user " + user.getDisplayName() + ".");
			return false;
		} else {
			return unfollowArtifact(user, followedArtifact);
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
	public List<FollowedArtifact> listFollowedArtifacts(User user) {
		return user.getFollowedArtifacts();
	}
	
	@Override
	public List<EmailAddress> listAdditionalEmails(User user) {
		return user.getAdditionalEmails();
	}

	@Override
	public List<ArtifactKey> listFollowedArtifactKeys(User user) {
		List<ArtifactKey> artifactKeyList = Lists.newArrayList();
		List<FollowedArtifact> followedArtifactList = listFollowedArtifacts(user);
		
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
	public void register(User user, String password) throws ServiceException, SecurityServiceException {
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
		setActive(user, true);
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
	public User getByOpenIdIdentifier(String openIdIdentifier) {
		List<User> userList = listByField(User_.openIdIdentifier, openIdIdentifier);
		return userList.size() > 0 ? userList.get(0) : null;
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
