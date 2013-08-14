package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.form.FormPanelMode;
import fr.openwide.core.wicket.more.markup.html.form.LabelPlaceholderBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.common.behavior.AuthenticatedOnlyBehavior;
import fr.openwide.maven.artifact.notifier.web.application.common.component.navigation.DynamicBookmarkablePageLink;
import fr.openwide.maven.artifact.notifier.web.application.common.component.navigation.DynamicPageParameters;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ItemAdditionalInformationPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ProjectDropDownChoice;
import fr.openwide.maven.artifact.notifier.web.application.project.form.ProjectFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.page.ProjectDescriptionPage;

public class ArtifactProjectPanel extends GenericPanel<Artifact> {

	private static final long serialVersionUID = -8479708798509150198L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactProjectPanel.class);
	
	@SpringBean
	private IArtifactService artifactService;
	
	@SpringBean
	private IProjectService projectService;
	
	public ArtifactProjectPanel(String id, IModel<Artifact> artifactModel) {
		super(id, artifactModel);
		
		// Has project container
		WebMarkupContainer hasProjectContainer = new WebMarkupContainer("hasProjectContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(artifactService.hasProject(getModelObject()));
			}
		};
		add(hasProjectContainer);
		
		//	>	Project link
		IModel<Project> projectModel = BindingModel.of(artifactModel, Binding.artifact().project());
		DynamicBookmarkablePageLink<Void> projectLink = new DynamicBookmarkablePageLink<Void>("projectLink", ProjectDescriptionPage.class,
				new DynamicPageParameters(LinkUtils.getDynamicProjectPageParameters(projectModel)));
		projectLink.setBody(new StringResourceModel("artifact.project.title.link", artifactModel));
		hasProjectContainer.add(projectLink);
		
		//	>	Project additional information
		hasProjectContainer.add(new ItemAdditionalInformationPanel("additionalInformationPanel",
				BindingModel.of(projectModel, Binding.project().additionalInformation())));
		
		// Create project popup
		ProjectFormPopupPanel createProjectPopup = new ProjectFormPopupPanel("createProjectPopup", FormPanelMode.ADD);
		add(createProjectPopup);
		
		// No project container
		WebMarkupContainer noProjectContainer = new WebMarkupContainer("noProjectContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!artifactService.hasProject(getModelObject()));
			}
		};
		noProjectContainer.add(new AuthenticatedOnlyBehavior());
		add(noProjectContainer);
		
		//	>	No project label
		noProjectContainer.add(new Label("noProject", new ResourceModel("artifact.project.noProject")));
		
		//	>	Create project button
		noProjectContainer.add(new Button("createProject").add(new AjaxModalOpenBehavior(createProjectPopup, MouseEvent.CLICK)));
		
		//	>	Project form
		IModel<Project> emptyProjectModel = new GenericEntityModel<Long, Project>(null);
		Form<Project> projectForm = new StatelessForm<Project>("projectForm", emptyProjectModel);
		noProjectContainer.add(projectForm);
		
		//	>	>	Project dropdown
		final ProjectDropDownChoice projectDropDown = new ProjectDropDownChoice("project", emptyProjectModel);
		projectDropDown.setRequired(true);
		projectDropDown.setLabel(new ResourceModel("artifact.project.chooseOne"));
		projectDropDown.add(new LabelPlaceholderBehavior());
		projectForm.add(projectDropDown);
		
		//	>	>	Project form AJAX submit link
		projectForm.add(new AjaxSubmitLink("addToProject", projectForm) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Artifact artifact = ArtifactProjectPanel.this.getModelObject();
				Project selectedProject = projectDropDown.getModelObject();
				
				if (selectedProject != null) {
					if (artifact.getProject() == null) {
						try {
							projectService.addArtifact(selectedProject, artifact);
							getSession().success(getString("artifact.project.add.success"));
						} catch (Exception e) {
							LOGGER.error("Unknown error occured while adding an artifact to a project", e);
							getSession().error(getString("artifact.project.add.error"));
						}
					} else {
						LOGGER.error("Artifact already added to a project");
						getSession().warn(getString("artifact.project.add.alreadyHasProject"));
					}
				}
				projectDropDown.setModelObject(null);
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
	}
}
