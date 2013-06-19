package fr.openwide.maven.artifact.notifier.core.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.junit.AbstractTestCase;
import fr.openwide.core.jpa.more.business.parameter.model.Parameter;
import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.core.jpa.security.business.authority.service.IAuthorityService;
import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;
import fr.openwide.maven.artifact.notifier.core.business.parameter.service.IParameterService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.test.config.spring.MavenArtifactNotifierCoreTestCommonConfig;

@ContextConfiguration(classes = MavenArtifactNotifierCoreTestCommonConfig.class)
public abstract class AbstractMavenArtifactNotifierTestCase extends AbstractTestCase {
	
	@Autowired
	protected IParameterService parameterService;

	@Autowired
	protected IUserService userService;
	
	@Autowired
	protected IUserGroupService userGroupService;
	
	@Autowired
	protected IAuthorityService authorityService;

	@Override
	public void init() throws ServiceException, SecurityServiceException {
		super.init();
		initAuthorities();
	}

	@Override
	protected void cleanAll() throws ServiceException, SecurityServiceException {
		cleanParameters();
		cleanUsers();
		cleanUserGroups();
		cleanAuthorities();
	}
	
	protected void cleanParameters() throws ServiceException, SecurityServiceException {
		for (Parameter parameter : parameterService.list()) {
			parameterService.delete(parameter);
		}
	}

	protected void cleanUsers() throws ServiceException, SecurityServiceException {
		for (User user : userService.list()) {
			userService.delete(user);
		}
	}

	protected void cleanUserGroups() throws ServiceException, SecurityServiceException {
		for (UserGroup userGroup : userGroupService.list()) {
			userGroupService.delete(userGroup);
		}
	}

	protected void cleanAuthorities() throws ServiceException, SecurityServiceException {
		for (Authority authority : authorityService.list()) {
			authorityService.delete(authority);
		}
	}

	private void initAuthorities() throws ServiceException, SecurityServiceException {
		authorityService.create(new Authority(CoreAuthorityConstants.ROLE_AUTHENTICATED));
	}
}
