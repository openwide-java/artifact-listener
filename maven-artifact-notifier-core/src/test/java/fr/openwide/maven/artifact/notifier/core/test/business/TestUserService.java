package fr.openwide.maven.artifact.notifier.core.test.business;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.test.AbstractMavenArtifactNotifierTestCase;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;

public class TestUserService extends AbstractMavenArtifactNotifierTestCase {

	@Test
	public void testUser() throws ServiceException, SecurityServiceException {
		
		{
			User user = new User();
			user.setUserName("firstname.lastname@test.fr");
			user.setFullName("firstname lastname");
			
			userService.create(user);
		}
		
		List<User> userList = userService.list();
		
		assertEquals(1, userList.size());
		assertEquals("firstname.lastname@test.fr", userList.get(0).getUserName());
		assertEquals("firstname lastname", userList.get(0).getFullName());
	}
}
