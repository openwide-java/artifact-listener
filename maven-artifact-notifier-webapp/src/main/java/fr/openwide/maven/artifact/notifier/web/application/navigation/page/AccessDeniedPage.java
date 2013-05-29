package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.core.wicket.more.application.CoreWicketAuthenticatedApplication;
import fr.openwide.core.wicket.more.markup.html.CoreWebPage;

public class AccessDeniedPage extends CoreWebPage {

	private static final long serialVersionUID = 4583415457223655426L;

	public AccessDeniedPage() {
		super(new PageParameters());
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		MavenArtifactNotifierSession.get().error(getString("access.denied"));
		redirect(CoreWicketAuthenticatedApplication.get().getSignInPageClass());
	}
}
