package fr.openwide.maven.artifact.notifier.web.application.auth.model;

import java.io.Serializable;

public class NormalizedOpenIdAttributes implements Serializable {
	
	private static final long serialVersionUID = -4289737969691961104L;
	
	private String openIdIdentifier;
	
	private String emailAddress;
	
	private String fullName;
	
	public NormalizedOpenIdAttributes(String openIdIdentifier, String emailAddress, String fullName) {
		this.openIdIdentifier = openIdIdentifier;
		this.emailAddress = emailAddress;
		this.fullName = fullName;
	}

	public String getOpenIdIdentifier() {
		return openIdIdentifier;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	
	public String getFullName() {
		return fullName;
	}
}