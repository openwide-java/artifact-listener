package fr.openwide.maven.artifact.notifier.web.application.auth.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.core.jpa.security.business.person.model.IPerson;
import fr.openwide.core.jpa.security.business.person.model.IPersonGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

public class OpenIdUserDetailsService implements UserDetailsService {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private RoleHierarchy roleHierarchy;
	
	@Override
	public UserDetails loadUserByUsername(String openIdIdentifier) throws UsernameNotFoundException, DisabledException {
		IPerson person = userService.getByOpenIdIdentifier(openIdIdentifier);

		if (person == null) {
			throw new UsernameNotFoundException("User not found for OpenID: " + openIdIdentifier);
		}
		
		if (!person.isActive()) {
			throw new DisabledException("User is disabled");
		}
		
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		
		addAuthorities(grantedAuthorities, person.getAuthorities());
		
		for (IPersonGroup personGroup : person.getPersonGroups()) {
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
