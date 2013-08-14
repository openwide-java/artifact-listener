package fr.openwide.maven.artifact.notifier.web.application.console.importation.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.wicket.more.console.template.ConsoleTemplate;
import fr.openwide.maven.artifact.notifier.web.application.console.importation.template.ConsoleImportTemplate;

public class ConsoleImportProjectVersionPage extends ConsoleImportTemplate {

	private static final long serialVersionUID = -6767518941118385548L;
	
	public ConsoleImportProjectVersionPage(PageParameters parameters) {
		super(parameters);
		
		addHeadPageTitleKey("console.import.projectVersion");
	}
	
	@Override
	protected Class<? extends ConsoleTemplate> getMenuItemPageClass() {
		return ConsoleImportProjectVersionPage.class;
	}
}
