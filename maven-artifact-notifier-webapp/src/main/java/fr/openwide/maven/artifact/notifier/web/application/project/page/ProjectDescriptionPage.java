package fr.openwide.maven.artifact.notifier.web.application.project.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.DashboardPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ProjectArtifactsPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ProjectDescriptionPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ProjectLinksPanel;
import fr.openwide.maven.artifact.notifier.web.application.project.form.ProjectFormPopupPanel;

public class ProjectDescriptionPage extends MainTemplate {

	private static final long serialVersionUID = 524541454604961458L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDescriptionPage.class);

	@SpringBean
	private IProjectService projectService;
	
	@SpringBean
	private IUserService userService;
	
	private IModel<Project> projectModel;
	
	public ProjectDescriptionPage(PageParameters parameters) {
		super(parameters);
		
		Project project = LinkUtils.extractProjectPageParameter(projectService, parameters, getApplication().getHomePage());
		projectModel = new GenericEntityModel<Long, Project>(project);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("project.list.pageTitle"), ProjectListPage.class));
		addBreadCrumbElement(new BreadCrumbElement(new StringResourceModel("project.description.pageTitle", projectModel), getPageClass(), parameters));
		
		add(new Label("pageTitle", new StringResourceModel("project.description.pageTitle", projectModel)));
		
		// Edit popup
		ProjectFormPopupPanel editProjectPopup = new ProjectFormPopupPanel("editProjectPopup", projectModel);
		add(editProjectPopup);
		
		// Edit button
		Button editButton = new Button("editButton");
		editButton.add(new AjaxModalOpenBehavior(editProjectPopup, MouseEvent.CLICK));
		add(editButton);
		
		// Follow
		AjaxLink<Project> follow = new AjaxLink<Project>("follow", projectModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.followProject(MavenArtifactNotifierSession.get().getUser(), getModelObject());
					target.add(getPage());
				} catch (Exception e) {
					LOGGER.error("Error occured while following project", e);
					getSession().error(getString("common.error.unexpected"));
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				Project project = getModelObject();
				setVisible(project != null && !userService.isFollowedProject(MavenArtifactNotifierSession.get().getUser(), project));
			}
		};
		add(follow);
		
		// Unfollow
		AjaxLink<Project> unfollow = new AjaxLink<Project>("unfollow", projectModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.unfollowProject(MavenArtifactNotifierSession.get().getUser(), getModelObject());
					target.add(getPage());
				} catch (Exception e) {
					LOGGER.error("Error occured while unfollowing project", e);
					getSession().error(getString("common.error.unexpected"));
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				Project project = getModelObject();
				setVisible(project != null && userService.isFollowedProject(MavenArtifactNotifierSession.get().getUser(), project));
			}
		};
		add(unfollow);
		
		// Followers count label
//		add(new CountLabel("followersCountLabel", "artifact.description.followers", new LoadableDetachableModel<Long>() {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected Long load() {
//				Artifact artifact = projectModel.getObject();
//				return artifact != null ? artifact.getFollowersCount() : 0;
//			}
//		}));
		
		add(new ProjectDescriptionPanel("projectDescriptionPanel", projectModel));
		add(new ProjectArtifactsPanel("projectArtifactsPanel", projectModel));
		add(new ProjectLinksPanel("projectLinksPanel", projectModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ProjectListPage.class;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (projectModel != null) {
			projectModel.detach();
		}
	}
}
