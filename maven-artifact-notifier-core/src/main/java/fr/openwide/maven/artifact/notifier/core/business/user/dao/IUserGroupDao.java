package fr.openwide.maven.artifact.notifier.core.business.user.dao;

import fr.openwide.core.jpa.business.generic.dao.IGenericEntityDao;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;

public interface IUserGroupDao extends IGenericEntityDao<Long, UserGroup> {

	UserGroup getByName(String name);
}
