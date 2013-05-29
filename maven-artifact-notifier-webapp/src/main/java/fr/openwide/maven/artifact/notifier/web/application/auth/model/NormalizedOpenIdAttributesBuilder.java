package fr.openwide.maven.artifact.notifier.web.application.auth.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import fr.openwide.core.spring.util.StringUtils;


public class NormalizedOpenIdAttributesBuilder {
	
	private Set<String> emailAddressAttributeNames = new HashSet<String>();
	
	private Set<String> firstNameAttributeNames = new HashSet<String>();
	
	private Set<String> lastNameAttributeNames = new HashSet<String>();
	
	private Set<String> fullNameAttributeNames = new HashSet<String>();

	public NormalizedOpenIdAttributes build(OpenIDAuthenticationToken openIdAuthenticationToken) {
		String userLocalIdentifier = openIdAuthenticationToken.getIdentityUrl();
		
		String emailAddress = getAttributeValue(openIdAuthenticationToken, emailAddressAttributeNames);
		String firstName = getAttributeValue(openIdAuthenticationToken, firstNameAttributeNames);
		String lastName = getAttributeValue(openIdAuthenticationToken, lastNameAttributeNames);
		String fullName = getAttributeValue(openIdAuthenticationToken, fullNameAttributeNames);
		
		if (!StringUtils.hasText(fullName) && (StringUtils.hasText(firstName) || StringUtils.hasText(lastName))) {
			fullName = buildFullName(firstName, lastName);
		}
		return new NormalizedOpenIdAttributes(userLocalIdentifier, emailAddress, fullName);
	}
	
	private String buildFullName(String firstName, String lastName) {
		StringBuilder builder = new StringBuilder();
		if (StringUtils.hasText(firstName)) {
			builder.append(firstName);
			builder.append(" ");
		}
		if (StringUtils.hasText(lastName) && !lastName.equals(firstName)) {
			builder.append(lastName);
		}
		return builder.toString().trim();
	}

	private String getAttributeValue(OpenIDAuthenticationToken openIdAuthenticationToken, Set<String> stringSet) {
		for (OpenIDAttribute openIDAttribute : openIdAuthenticationToken.getAttributes()) {
			if (attributeHasValue(openIDAttribute) && stringSet.contains(openIDAttribute.getName())) {
				return openIDAttribute.getValues().get(0);
			}
		}
		return null;
	}
	
	private boolean attributeHasValue(OpenIDAttribute openIDAttribute) {
		return openIDAttribute.getValues() != null && openIDAttribute.getValues().size() > 0;
	}

	public void setEmailAddressAttributeNames(Set<String> emailAddressAttributeNames) {
		this.emailAddressAttributeNames = emailAddressAttributeNames;
	}

	public void setFirstNameAttributeNames(Set<String> firstNameAttributeNames) {
		this.firstNameAttributeNames = firstNameAttributeNames;
	}

	public void setLastNameAttributeNames(Set<String> lastNameAttributeNames) {
		this.lastNameAttributeNames = lastNameAttributeNames;
	}

	public void setFullNameAttributeNames(Set<String> fullNameAttributeNames) {
		this.fullNameAttributeNames = fullNameAttributeNames;
	}
}