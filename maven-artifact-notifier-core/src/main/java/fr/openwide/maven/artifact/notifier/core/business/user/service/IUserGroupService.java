package fr.openwide.maven.artifact.notifier.core.business.user.service;

import java.util.List;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;

public interface IUserGroupService extends IGenericEntityService<Long, UserGroup> {

	void addPerson(UserGroup group, User user) throws ServiceException, SecurityServiceException;

	void removePerson(UserGroup group, User user) throws ServiceException, SecurityServiceException;

	List<UserGroup> searchAutocomplete(String searchPattern) throws ServiceException, SecurityServiceException;
}
