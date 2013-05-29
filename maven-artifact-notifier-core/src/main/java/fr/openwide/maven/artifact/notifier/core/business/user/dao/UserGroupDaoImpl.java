package fr.openwide.maven.artifact.notifier.core.business.user.dao;

import org.springframework.stereotype.Repository;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup_;

@Repository("personGroupDao")
public class UserGroupDaoImpl extends GenericEntityDaoImpl<Long, UserGroup> implements IUserGroupDao {

	public UserGroupDaoImpl() {
		super();
	}

	@Override
	public UserGroup getByName(String name) {
		return getByField(UserGroup_.name, name);
	}
}
