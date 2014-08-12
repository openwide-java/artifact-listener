package fr.openwide.maven.artifact.notifier.core.business.user.dao;

import fr.openwide.core.jpa.security.business.person.dao.IGenericUserGroupDao;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;

public interface IUserGroupDao extends IGenericUserGroupDao<UserGroup, User> {

	UserGroup getByName(String name);
}
