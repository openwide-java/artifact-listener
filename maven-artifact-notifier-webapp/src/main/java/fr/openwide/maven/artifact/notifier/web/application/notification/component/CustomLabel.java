package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public class CustomLabel extends Label {
	
	private static final long serialVersionUID = 519232144606598731L;
	
	public CustomLabel(final String id, Serializable label, String style) {
		this(id, Model.of(label), style);
	}
	
	public CustomLabel(final String id, IModel<?> model, String style) {
		super(id, model);
		add(new StyleAttributeAppender(style));
	}
	
}
