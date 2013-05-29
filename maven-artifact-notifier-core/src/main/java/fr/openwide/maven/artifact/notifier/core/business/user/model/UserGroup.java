package fr.openwide.maven.artifact.notifier.core.business.user.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

import org.bindgen.Bindable;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;
import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.core.jpa.security.business.person.model.IPersonGroup;

@Entity
@Indexed
@Bindable
public class UserGroup extends GenericEntity<Long, UserGroup> implements IPersonGroup {
	private static final long serialVersionUID = 2156717229285615454L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;

	@Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT))
	private String name;

	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	@ManyToMany(mappedBy = "userGroups")
	@OrderBy("userName")
	private List<User> persons = Lists.newLinkedList();

	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	@ManyToMany
	@OrderBy("name")
	private Set<Authority> authorities = new LinkedHashSet<Authority>();

	@Type(type = "org.hibernate.type.StringClobType")
	private String description;

	@Column(nullable = false)
	private Boolean locked = false;

	public UserGroup() {
	}

	public UserGroup(String name) {
		super();
		
		setName(name);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getPersons() {
		return persons;
	}

	public void setPersons(List<User> persons) {
		this.persons = persons;
	}

	@Override
	public String getDisplayName() {
		return this.getName();
	}

	@Override
	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public void addAuthority(Authority authority) {
		this.authorities.add(authority);
	}

	public void removeAuthority(Authority authority) {
		this.authorities.remove(authority);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	@Override
	public int compareTo(UserGroup group) {
		if (this == group) {
			return 0;
		}
		return DEFAULT_STRING_COLLATOR.compare(this.getName(), group.getName());
	}

	@Override
	public String getNameForToString() {
		return getName();
	}
}
