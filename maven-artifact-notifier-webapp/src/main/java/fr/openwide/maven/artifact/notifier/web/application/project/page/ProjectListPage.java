package fr.openwide.maven.artifact.notifier.web.application.project.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;

import fr.openwide.core.wicket.more.markup.html.form.FormPanelMode;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ProjectPortfolioPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ProjectSearchPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.form.ProjectFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.model.ProjectDataProvider;

public class ProjectListPage extends MainTemplate {

	private static final long serialVersionUID = 1040259828918988269L;

	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;

	public ProjectListPage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("project.list.pageTitle"),
				ProjectListPage.class));
		
		// Add project button
		final ProjectFormPopupPanel addProjectPopup = new ProjectFormPopupPanel("addProjectPopup", FormPanelMode.ADD);
		add(addProjectPopup);
		add(new Button("addProject").add(new AjaxModalOpenBehavior(addProjectPopup, MouseEvent.CLICK)));

		// Page content
		IModel<String> searchTermModel = Model.of("");
		ProjectPortfolioPanel portfolioPanel = new ProjectPortfolioPanel("portfolio",
				new ProjectDataProvider(searchTermModel), configurer.getPortfolioItemsPerPage());
		add(portfolioPanel);
		
		add(new ProjectSearchPanel("searchPanel", portfolioPanel.getPageable(), searchTermModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ProjectListPage.class;
	}
}
