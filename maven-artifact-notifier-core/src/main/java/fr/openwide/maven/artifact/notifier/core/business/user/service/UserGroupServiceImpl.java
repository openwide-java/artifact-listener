package fr.openwide.maven.artifact.notifier.core.business.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.search.service.IHibernateSearchService;
import fr.openwide.maven.artifact.notifier.core.business.user.dao.IUserGroupDao;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

@Service("personGroupService")
public class UserGroupServiceImpl extends GenericEntityServiceImpl<Long, UserGroup>
		implements IUserGroupService {

	@Autowired
	private IHibernateSearchService hibernateSearchService;

	@Autowired
	private IUserService userService;

	@Autowired
	public UserGroupServiceImpl(IUserGroupDao userGroupDao) {
		super(userGroupDao);
	}

	@Override
	public void addPerson(UserGroup group, User user)
			throws ServiceException, SecurityServiceException {
		user.getUserGroups().add(group);
		userService.update(user);
	}

	@Override
	public void removePerson(UserGroup group, User user)
			throws ServiceException, SecurityServiceException {
		user.getUserGroups().remove(group);
		userService.update(user);
	}

	@Override
	public List<UserGroup> searchAutocomplete(String searchPattern) throws ServiceException, SecurityServiceException {
		String[] searchFields = new String[] { Binding.userGroup().name().getPath() };
		
		return hibernateSearchService.searchAutocomplete(getObjectClass(), searchFields, searchPattern);
	}
}
