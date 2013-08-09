package fr.openwide.maven.artifact.notifier.web.application.project.component;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.html.form.FormPanelMode;
import fr.openwide.core.wicket.more.markup.html.list.GenericPortfolioPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.maven.artifact.notifier.web.application.project.form.ProjectFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.page.ProjectDescriptionPage;

public class ProjectPortfolioPanel extends GenericPortfolioPanel<Project> {

	private static final long serialVersionUID = 4887494623297671798L;

	@SpringBean
	private IProjectService projectService;
	
	private ProjectFormPopupPanel editProjectPopup;

	public ProjectPortfolioPanel(String id, IDataProvider<Project> dataProvider, int itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
		
		editProjectPopup = new ProjectFormPopupPanel("editProjectPopup", FormPanelMode.EDIT);
		add(editProjectPopup);
	}

	@Override
	protected void addItemColumns(Item<Project> item, IModel<? extends Project> projectModel) {
		Link<Project> projectLink = new BookmarkablePageLink<Project>("projectLink", ProjectDescriptionPage.class, 
				LinkUtils.getProjectPageParameters(projectModel.getObject()));
		projectLink.add(new Label("name", BindingModel.of(projectModel, Binding.project().name())));
		item.add(projectLink);
		
		item.add(new Label("nbVersions", BindingModel.of(projectModel, Binding.project().versions().size())));
		
		item.add(new Label("nbArtifacts", BindingModel.of(projectModel, Binding.project().artifacts().size())));
	}

	@Override
	protected boolean isActionAvailable() {
		return true;
	}

	@Override
	protected boolean isDeleteAvailable() {
		return true;
	}

	@Override
	protected boolean isEditAvailable() {
		return true;
	}

	@Override
	protected boolean hasWritePermissionOn(IModel<? extends Project> projectModel) {
		return MavenArtifactNotifierSession.get().hasRoleAdmin();
	}
	
	@Override
	protected MarkupContainer getActionLink(String id, IModel<? extends Project> projectModel) {
		return new BookmarkablePageLink<Artifact>(id, ProjectDescriptionPage.class,
				LinkUtils.getProjectPageParameters(projectModel.getObject()));
	}
	
	@Override
	protected MarkupContainer getEditLink(String id, final IModel<? extends Project> projectModel) {
		Button editLink = new Button(id);
		editLink.add(new AjaxModalOpenBehavior(editProjectPopup, MouseEvent.CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onShow(AjaxRequestTarget target) {
				editProjectPopup.getModel().setObject(projectModel.getObject());
			}
		});
		return editLink;
	}
	
	@Override
	protected void doDeleteItem(IModel<? extends Project> projectModel) throws ServiceException, SecurityServiceException {
		projectService.delete(projectModel.getObject());
	}

	@Override
	protected IModel<String> getDeleteConfirmationTitleModel(IModel<? extends Project> projectModel) {
		return new StringResourceModel("project.delete.confirmation.title", projectModel);
	}

	@Override
	protected IModel<String> getDeleteConfirmationTextModel(IModel<? extends Project> projectModel) {
		return new StringResourceModel("project.delete.confirmation.text", projectModel);
	}
}
