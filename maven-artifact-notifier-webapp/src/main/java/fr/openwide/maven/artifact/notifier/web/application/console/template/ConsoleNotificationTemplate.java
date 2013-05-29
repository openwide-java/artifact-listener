package fr.openwide.maven.artifact.notifier.web.application.console.template;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.wicket.more.console.template.ConsoleTemplate;
import fr.openwide.maven.artifact.notifier.web.application.console.page.ConsoleNotificationIndexPage;

public abstract class ConsoleNotificationTemplate extends ConsoleTemplate {

	private static final long serialVersionUID = -3192604063259001201L;
	
	public ConsoleNotificationTemplate(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	protected Class<? extends ConsoleTemplate> getMenuSectionPageClass() {
		return ConsoleNotificationIndexPage.class;
	}
}
