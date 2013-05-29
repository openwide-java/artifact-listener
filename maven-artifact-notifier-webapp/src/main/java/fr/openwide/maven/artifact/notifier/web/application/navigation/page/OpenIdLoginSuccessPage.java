package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import fr.openwide.core.wicket.more.markup.html.CoreWebPage;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;

public class OpenIdLoginSuccessPage extends CoreWebPage {
	
	private static final long serialVersionUID = -875304387617628398L;
	
	public OpenIdLoginSuccessPage() {
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		MavenArtifactNotifierSession.get().authenticateOpenId();
		
		redirect(DashboardPage.class);
	}
}