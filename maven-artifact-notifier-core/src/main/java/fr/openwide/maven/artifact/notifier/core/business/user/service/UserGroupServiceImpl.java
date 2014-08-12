package fr.openwide.maven.artifact.notifier.core.business.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.search.service.IHibernateSearchService;
import fr.openwide.core.jpa.security.business.person.service.GenericUserGroupServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.user.dao.IUserGroupDao;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;

@Service("personGroupService")
public class UserGroupServiceImpl extends GenericUserGroupServiceImpl<UserGroup, User>
		implements IUserGroupService {

	@Autowired
	private IHibernateSearchService hibernateSearchService;

	@Autowired
	private IUserService userService;

	@Autowired
	public UserGroupServiceImpl(IUserGroupDao userGroupDao) {
		super(userGroupDao);
	}

}
