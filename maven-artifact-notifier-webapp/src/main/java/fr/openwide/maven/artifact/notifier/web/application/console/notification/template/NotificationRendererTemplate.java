package fr.openwide.maven.artifact.notifier.web.application.console.notification.template;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;

public class NotificationRendererTemplate extends WebPage {

	private static final long serialVersionUID = -3192604063259001201L;
	
	public NotificationRendererTemplate(PageParameters parameters) {
		super(parameters);
		
		MarkupContainer htmlRootElement = new TransparentWebMarkupContainer("htmlRootElement");
		htmlRootElement.add(AttributeAppender.append("lang", MavenArtifactNotifierSession.get().getLocale().getLanguage()));
		add(htmlRootElement);
		
		add(new Label("headPageTitle", new ResourceModel("console.notifications")));
	}
}
