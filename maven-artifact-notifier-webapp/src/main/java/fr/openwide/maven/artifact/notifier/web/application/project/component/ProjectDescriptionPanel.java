package fr.openwide.maven.artifact.notifier.web.application.project.component;

import java.util.Set;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.wiquery.core.events.MouseEvent;

import fr.openwide.core.wicket.markup.html.basic.HideableExternalLink;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.form.FormPanelMode;
import fr.openwide.core.wicket.more.markup.html.image.BooleanIcon;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.CollectionToListWrapperModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersionStatus;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactVersionTagPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.component.AuthenticatedOnlyButton;
import fr.openwide.maven.artifact.notifier.web.application.project.form.ProjectVersionFormPopupPanel;

public class ProjectDescriptionPanel extends GenericPanel<Project> {

	private static final long serialVersionUID = 7757299234352613717L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDescriptionPanel.class);

	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	@SpringBean
	private IProjectService projectService;
	
	private ProjectVersionFormPopupPanel projectVersionAddPopup;
	
	private ProjectVersionFormPopupPanel projectVersionEditPopup;
	
	public ProjectDescriptionPanel(String id, IModel<? extends Project> projectModel) {
		super(id, projectModel);

		// Project version popups
		projectVersionAddPopup = new ProjectVersionFormPopupPanel("projectVersionAddPopup", FormPanelMode.ADD) {
			private static final long serialVersionUID = 1L;

			@Override
			protected Project getProject() {
				return ProjectDescriptionPanel.this.getModelObject();
			}
		};
		add(projectVersionAddPopup);
		
		projectVersionEditPopup = new ProjectVersionFormPopupPanel("projectVersionEditPopup", FormPanelMode.EDIT);
		add(projectVersionEditPopup);
		
		// Add action
		Button addButton = new AuthenticatedOnlyButton("add") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(MavenArtifactNotifierSession.get().hasRoleAdmin());
			}
		};
		addButton.add(new AjaxModalOpenBehavior(projectVersionAddPopup, MouseEvent.CLICK));
		add(addButton);
		
		// Versions
		IModel<Set<ProjectVersion>> setModel = BindingModel.of(getModel(), Binding.project().versions());
		add(new ListView<ProjectVersion>("projectVersions", CollectionToListWrapperModel.of(setModel)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ProjectVersion> item) {
				item.add(new ArtifactVersionTagPanel("version", BindingModel.of(item.getModel(), Binding.projectVersion().version())));
				item.add(new DateLabel("lastUpdateDate", BindingModel.of(item.getModel(), Binding.projectVersion().lastUpdateDate()),
						DatePattern.SHORT_DATE));
				
				// Changelog link
				item.add(new HideableExternalLink("changelogLink",
						BindingModel.of(item.getModel(), Binding.projectVersion().additionalInformation().changelogUrl().url())));
				
				// Release notes link
				item.add(new HideableExternalLink("releaseNotesLink",
						BindingModel.of(item.getModel(), Binding.projectVersion().additionalInformation().releaseNotesUrl().url())));

				// Announce link
				item.add(new HideableExternalLink("announceLink",
						BindingModel.of(item.getModel(), Binding.projectVersion().additionalInformation().announceUrl().url())));
				
				// Status
				item.add(new BooleanIcon("centralAvailability", new LoadableDetachableModel<Boolean>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected Boolean load() {
						IModel<ProjectVersionStatus> statusModel = BindingModel.of(item.getModel(), Binding.projectVersion().status());
						return ProjectVersionStatus.PUBLISHED_ON_MAVEN_CENTRAL.equals(statusModel.getObject());
					}
				}));
				
				// Edit action
				Button editButton = new AuthenticatedOnlyButton("edit");
				editButton.add(new AjaxModalOpenBehavior(projectVersionEditPopup, MouseEvent.CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onShow(AjaxRequestTarget target) {
						super.onShow(target);
						projectVersionEditPopup.getModel().setObject(item.getModelObject());
					}
				});
				item.add(editButton);
				
				// Delete action
				IModel<String> confirmationTextModel = new StringResourceModel("project.version.delete.confirmation.text", item.getModel());
				item.add(new AjaxConfirmLink<ProjectVersion>("delete", item.getModel(),
						new ResourceModel("project.version.delete.confirmation.title"),
						confirmationTextModel,
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"), null, false) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							Project project = ProjectDescriptionPanel.this.getModelObject();
							ProjectVersion projectVersion = getModelObject();
							
							projectService.deleteProjectVersion(project, projectVersion);
							Session.get().success(getString("project.version.delete.success"));
						} catch (Exception e) {
							LOGGER.error("Error occured while removing artifact", e);
							Session.get().error(getString("project.version.delete.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
					
					@Override
					protected void onConfigure() {
						super.onConfigure();
						setVisible(MavenArtifactNotifierSession.get().hasRoleAdmin());
					}
				});
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!getModelObject().isEmpty());
			}
		});
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(getModelObject().getVersions().isEmpty());
			}
		});
	}
}
