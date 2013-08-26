package fr.openwide.maven.artifact.notifier.web.application.console.importation.page.resource;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.UrlResourceReference;

public class ProjectImportModelResourceReference extends UrlResourceReference {

	private static final long serialVersionUID = 2262127218326542618L;
	
	private static final ProjectImportModelResourceReference INSTANCE = new ProjectImportModelResourceReference();
	
	private ProjectImportModelResourceReference() {
		super(Url.parse("../../../static/console/import/project/resource/project-import-model.xls"));
	}
	
	public static ProjectImportModelResourceReference get() {
		return INSTANCE;
	}
}
