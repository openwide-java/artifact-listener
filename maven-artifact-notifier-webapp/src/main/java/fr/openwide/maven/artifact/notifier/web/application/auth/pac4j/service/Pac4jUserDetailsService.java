package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service;

import java.util.HashSet;
import java.util.Set;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.core.jpa.security.business.person.model.IGroupedUser;
import fr.openwide.core.jpa.security.business.person.model.IUserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.model.AuthenticationType;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

public class Pac4jUserDetailsService implements AuthenticationUserDetailsService<ClientAuthenticationToken> {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private RoleHierarchy roleHierarchy;
	
	@Override
	public UserDetails loadUserDetails(ClientAuthenticationToken token) throws UsernameNotFoundException {
		CommonProfile commonProfile = (CommonProfile) token.getUserProfile();
		
		IGroupedUser<?> person = userService.getByRemoteIdentifier(commonProfile.getId());
		
		if (person == null) {
			throw new UsernameNotFoundException("User not found for: " + token.getPrincipal());
		}
		
		if (!person.isActive()) {
			throw new DisabledException("User is disabled");
		}
		
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		
		addAuthorities(grantedAuthorities, person.getAuthorities());
		
		for (IUserGroup personGroup : person.getGroups()) {
			addAuthorities(grantedAuthorities, personGroup.getAuthorities());
		}
		
		User userDetails = new User(person.getUserName(), person.getPasswordHash(), person.isActive(), true, true, true, 
				roleHierarchy.getReachableGrantedAuthorities(grantedAuthorities));
		
		return userDetails;
	}
	
	protected void addAuthorities(Set<GrantedAuthority> grantedAuthorities, Set<Authority> authorities) {
		for (Authority authority : authorities) {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
		}
	}
}
