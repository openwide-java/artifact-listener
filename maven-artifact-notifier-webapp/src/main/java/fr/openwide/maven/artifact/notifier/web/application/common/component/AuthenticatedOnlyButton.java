package fr.openwide.maven.artifact.notifier.web.application.common.component;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;

import fr.openwide.maven.artifact.notifier.web.application.common.behavior.AuthenticatedOnlyBehavior;

public class AuthenticatedOnlyButton extends Button {

	private static final long serialVersionUID = -7586843919098210316L;

	public AuthenticatedOnlyButton(String id) {
		super(id);
		
		add(new AuthenticatedOnlyBehavior());
	}

	public AuthenticatedOnlyButton(String id, IModel<String> model) {
		super(id, model);
		
		add(new AuthenticatedOnlyBehavior());
	}
}
