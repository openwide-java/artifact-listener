package fr.openwide.maven.artifact.notifier.core.business.user.service;

import fr.openwide.core.jpa.security.business.person.service.IGenericUserGroupService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;

public interface IUserGroupService extends IGenericUserGroupService<UserGroup, User> {

}
