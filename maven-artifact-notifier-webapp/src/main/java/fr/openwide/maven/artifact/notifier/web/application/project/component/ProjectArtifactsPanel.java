package fr.openwide.maven.artifact.notifier.web.application.project.component;

import java.util.List;
import java.util.Set;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.form.LabelPlaceholderBehavior;
import fr.openwide.core.wicket.more.markup.html.select2.AbstractLongIdGenericEntitySelect2AjaxAdapter;
import fr.openwide.core.wicket.more.markup.html.select2.util.DropDownChoiceWidth;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.CollectionToListWrapperModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.core.wicket.more.model.ReadOnlyModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactDropDownChoice;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactFollowActionsPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.behavior.AuthenticatedOnlyBehavior;

public class ProjectArtifactsPanel extends GenericPanel<Project> {

	private static final long serialVersionUID = 4341748125123497634L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectArtifactsPanel.class);

	@SpringBean
	private IProjectService projectService;
	
	@SpringBean
	private IUserService userService;

	private ListView<Artifact> artifactListView;

	public ProjectArtifactsPanel(String id, IModel<Project> projectModel) {
		super(id, projectModel);
		
		// Artifacts list
		IModel<Set<Artifact>> setModel = BindingModel.of(getModel(), Binding.project().artifacts());
		artifactListView = new ListView<Artifact>("artifacts", CollectionToListWrapperModel.of(setModel)) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final ListItem<Artifact> item) {
				item.setOutputMarkupId(true);
				
				Link<Void> artifactLink = ArtifactDescriptionPage
						.linkDescriptor(ReadOnlyModel.of(item.getModelObject()))
						.link("artifactLink");
				artifactLink.add(new Label("id", new StringResourceModel("project.description.artifacts.artifact", item.getModel())));
				item.add(artifactLink);
				
				// Follow actions
				item.add(new ArtifactFollowActionsPanel("followActions", item.getModel()));
				
				// Remove link
				IModel<String> confirmationTextModel = new StringResourceModel("project.description.artifacts.remove.confirmation.text", item.getModel());
				item.add(new AjaxConfirmLink<Artifact>("removeLink", item.getModel(),
						new ResourceModel("project.description.artifacts.remove.confirmation.title"),
						confirmationTextModel,
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"), null, false) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							Project project = ProjectArtifactsPanel.this.getModelObject();
							Artifact artifact = getModelObject();
							
							projectService.removeArtifact(project, artifact);
							Session.get().success(getString("project.description.artifacts.remove.success"));
						} catch (Exception e) {
							LOGGER.error("Error occured while removing artifact", e);
							Session.get().error(getString("project.description.artifacts.remove.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
					
					@Override
					protected void onConfigure() {
						super.onConfigure();
						setVisible(MavenArtifactNotifierSession.get().hasRoleAdmin());
					}
				}.add(new AuthenticatedOnlyBehavior()));
			}
		};
		add(artifactListView);
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(artifactListView.size() <= 0);
			}
		});
		
		// Add artifact form
		IModel<Artifact> emptyArtifactModel = new GenericEntityModel<Long, Artifact>(null);
		
		final ArtifactDropDownChoice artifactDropDown = new ArtifactDropDownChoice("artifact", emptyArtifactModel,
				new ProjectArtifactSelect2AjaxAdapter(ArtifactDropDownChoice.CHOICE_RENDERER));
		artifactDropDown.setWidth(DropDownChoiceWidth.NORMAL);
		artifactDropDown.setRequired(true);
		artifactDropDown.setLabel(new ResourceModel("project.description.artifacts.chooseOne"));
		artifactDropDown.add(new LabelPlaceholderBehavior());
		artifactDropDown.add(new AuthenticatedOnlyBehavior());
		
		final Form<Artifact> addArtifactForm = new StatelessForm<Artifact>("addArtifactForm", emptyArtifactModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(MavenArtifactNotifierSession.get().hasRoleAdmin());
			}
		};
		addArtifactForm.add(artifactDropDown);
		addArtifactForm.add(new AjaxSubmitLink("addArtifactLink", addArtifactForm) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Project project = ProjectArtifactsPanel.this.getModelObject();
				Artifact selectedArtifact = artifactDropDown.getModelObject();
				
				if (selectedArtifact != null) {
					if (selectedArtifact.getProject() == null) {
						try {
							projectService.addArtifact(project, selectedArtifact);
							getSession().success(getString("project.description.artifacts.add.success"));
						} catch (Exception e) {
							LOGGER.error("Unknown error occured while adding an artifact to a project", e);
							getSession().error(getString("project.description.artifacts.add.error"));
						}
					} else {
						LOGGER.error("Artifact already added to a project");
						getSession().warn(getString("project.description.artifacts.add.alreadyHasProject"));
					}
				}
				artifactDropDown.setModelObject(null);
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(MavenArtifactNotifierSession.get().hasRoleAdmin());
			}
		});
		addArtifactForm.add(new AuthenticatedOnlyBehavior());
		add(addArtifactForm);
	}
	
	private class ProjectArtifactSelect2AjaxAdapter extends AbstractLongIdGenericEntitySelect2AjaxAdapter<Artifact> {

		private static final long serialVersionUID = -8852109526432427612L;
		
		@SpringBean
		private IArtifactService artifactService;

		public ProjectArtifactSelect2AjaxAdapter(IChoiceRenderer<Artifact> choiceRenderer) {
			super(Artifact.class, choiceRenderer);
		}

		@Override
		public List<Artifact> getChoices(int start, int count, String term) {
			try {
				return artifactService.searchAutocompleteWithoutProject(term, count, start);
			} catch (ServiceException e) {
				LOGGER.error("Error while searching for artifacts");
				return Lists.newArrayListWithExpectedSize(0);
			}
		}
	}
}
