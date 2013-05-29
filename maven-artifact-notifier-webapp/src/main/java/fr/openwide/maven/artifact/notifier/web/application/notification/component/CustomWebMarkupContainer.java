package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.markup.html.WebMarkupContainer;

import fr.openwide.maven.artifact.notifier.web.application.notification.behavior.StyleAttributeAppender;

public class CustomWebMarkupContainer extends WebMarkupContainer {
	
	private static final long serialVersionUID = 3955262127059805260L;
	
	public CustomWebMarkupContainer(String id, String style) {
		super(id);
		add(new StyleAttributeAppender(style));
	}
	
}
