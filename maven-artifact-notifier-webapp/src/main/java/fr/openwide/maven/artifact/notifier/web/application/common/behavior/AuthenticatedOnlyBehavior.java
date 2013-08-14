package fr.openwide.maven.artifact.notifier.web.application.common.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.Behavior;

public class AuthenticatedOnlyBehavior extends Behavior {

	private static final long serialVersionUID = -8533644530041049668L;

	@Override
	public void bind(Component component) {
		super.bind(component);
		component.setVisibilityAllowed(isAuthenticated());
	}
	
	protected boolean isAuthenticated() {
		return AuthenticatedWebSession.exists() && AuthenticatedWebSession.get().isSignedIn();
	}
}
