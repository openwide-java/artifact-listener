package fr.openwide.maven.artifact.notifier.core.business.user.dao;

import org.springframework.stereotype.Repository;

import fr.openwide.core.jpa.security.business.person.dao.GenericUserGroupDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup_;

@Repository("personGroupDao")
public class UserGroupDaoImpl extends GenericUserGroupDaoImpl<UserGroup, User> implements IUserGroupDao {

	public UserGroupDaoImpl() {
		super();
	}

	@Override
	public UserGroup getByName(String name) {
		return getByField(UserGroup_.name, name);
	}
}
