package fr.openwide.maven.artifact.notifier.core.business.user.model;

public enum AuthenticationType {
	LOCAL,
	
	OAUTH2_GOOGLE,
	
	TWITTER,
	
	GITHUB,
	
	OPENID,
	
	@Deprecated
	OPENID_GOOGLE
}
