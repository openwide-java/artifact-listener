package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;

public class Pac4jLoginSuccessPage extends AbstractLoginSuccessPage {
	
	private static final long serialVersionUID = -875304387617628398L;
	
	public Pac4jLoginSuccessPage() {
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		MavenArtifactNotifierSession.get().authenticatePac4j();
		
		redirectToSavedPage();
	}
}