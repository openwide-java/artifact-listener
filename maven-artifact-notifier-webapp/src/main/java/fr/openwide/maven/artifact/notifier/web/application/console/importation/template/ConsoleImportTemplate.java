package fr.openwide.maven.artifact.notifier.web.application.console.importation.template;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.wicket.more.console.template.ConsoleTemplate;
import fr.openwide.maven.artifact.notifier.web.application.console.importation.page.ConsoleImportProjectPage;

public abstract class ConsoleImportTemplate extends ConsoleTemplate {

	private static final long serialVersionUID = -3192604063259001201L;
	
	public ConsoleImportTemplate(PageParameters parameters) {
		super(parameters);
		
		addHeadPageTitleKey("console.import");
	}
	
	@Override
	protected Class<? extends ConsoleTemplate> getMenuSectionPageClass() {
		return ConsoleImportProjectPage.class;
	}
}
