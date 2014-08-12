package fr.openwide.maven.artifact.notifier.core.business.user.model;

import javax.persistence.Entity;

import org.bindgen.Bindable;
import org.hibernate.search.annotations.Indexed;

import fr.openwide.core.jpa.security.business.person.model.GenericUserGroup;

@Entity
@Indexed
@Bindable
public class UserGroup extends GenericUserGroup<UserGroup, User> {
	private static final long serialVersionUID = 2156717229285615454L;

	public UserGroup() {
	}

	public UserGroup(String name) {
		super(name);
	}

	@Override
	protected UserGroup thisAsConcreteType() {
		return this;
	}
	
}
