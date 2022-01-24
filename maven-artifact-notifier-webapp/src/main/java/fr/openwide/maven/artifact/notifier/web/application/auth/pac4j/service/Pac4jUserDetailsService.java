package fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.service;

import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.core.jpa.security.business.person.model.IGroupedUser;
import fr.openwide.core.jpa.security.business.person.model.IUserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.springframework.security.authentication.Pac4jAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class Pac4jUserDetailsService implements AuthenticationUserDetailsService<Pac4jAuthenticationToken> {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private RoleHierarchy roleHierarchy;
	
	@Override
	public UserDetails loadUserDetails(Pac4jAuthenticationToken token) throws UsernameNotFoundException {
		CommonProfile commonProfile = token.getProfile();

		IGroupedUser<?> person = userService.getByRemoteIdentifier(commonProfile.getId());
		
		if (person == null) {
			throw new UsernameNotFoundException("User not found for: " + token.getPrincipal());
		}
		
		if (!person.isActive()) {
			throw new DisabledException("User is disabled");
		}
		
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		
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
