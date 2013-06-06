package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;

public class OpenIdLoginSuccessPage extends AbstractLoginSuccessPage {
	
	private static final long serialVersionUID = -875304387617628398L;
	
	public OpenIdLoginSuccessPage() {
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		MavenArtifactNotifierSession.get().authenticateOpenId();
		
		redirectToSavedPage();
	}
}