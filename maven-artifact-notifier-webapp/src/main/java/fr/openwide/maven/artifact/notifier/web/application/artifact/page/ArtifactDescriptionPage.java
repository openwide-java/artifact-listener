package fr.openwide.maven.artifact.notifier.web.application.artifact.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactDescriptionPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.DeprecatedArtifactPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.FollowedArtifactNotificationRulesPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactDeprecationFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.DashboardPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ArtifactDescriptionPage extends MainTemplate {

	private static final long serialVersionUID = 2693888834363896915L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactDescriptionPage.class);

	@SpringBean
	private IArtifactService artifactService;
	
	@SpringBean
	private IUserService userService;
	
	private IModel<Artifact> artifactModel;
	
	private IModel<FollowedArtifact> followedArtifactModel;
	
	public ArtifactDescriptionPage(PageParameters parameters) {
		super(parameters);
		
		Artifact artifact = LinkUtils.extractArtifactPageParameter(artifactService, parameters, getApplication().getHomePage());
		artifactModel = new GenericEntityModel<Long, Artifact>(artifact);
		
		followedArtifactModel = new LoadableDetachableModel<FollowedArtifact>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected FollowedArtifact load() {
				return userService.getFollowedArtifact(MavenArtifactNotifierSession.get().getUser(), getArtifactModel().getObject());
			}
		};
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("dashboard.pageTitle"), DashboardPage.class));
		addBreadCrumbElement(new BreadCrumbElement(new StringResourceModel("artifact.description.pageTitle", artifactModel), getPageClass(), parameters));
		
		add(new Label("pageTitle", new StringResourceModel("artifact.description.pageTitle", artifactModel)));
		
		// Deprecation popup
		ArtifactDeprecationFormPopupPanel deprecationPopup = new ArtifactDeprecationFormPopupPanel("deprecationPopup", artifactModel);
		add(deprecationPopup);
		
		Button deprecate = new Button("deprecation");
		deprecate.add(new AjaxModalOpenBehavior(deprecationPopup, MouseEvent.CLICK) {
			private static final long serialVersionUID = 5414159291353181776L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		deprecate.add(new Label("deprecationLabel", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				if (ArtifactDeprecationStatus.DEPRECATED.equals(getArtifactModel().getObject().getDeprecationStatus())) {
					return getString("artifact.deprecation.unmarkAsDeprecated");
				}
				return getString("artifact.deprecation.markAsDeprecated");
			}
		}));
		add(deprecate);
		
		// Follow
		AjaxLink<Artifact> follow = new AjaxLink<Artifact>("follow", artifactModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.followArtifact(MavenArtifactNotifierSession.get().getUser(), getModelObject());
					target.add(getPage());
				} catch (AlreadyFollowedArtifactException e) {
					getSession().warn(getString("artifact.follow.alreadyFollower"));
					target.add(getPage());
				} catch (Exception e) {
					LOGGER.error("Error occured while following artifact", e);
					getSession().error(getString("common.error.unexpected"));
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				Artifact artifact = getModelObject();
				boolean isDeprecated = artifact != null && ArtifactDeprecationStatus.DEPRECATED.equals(artifact.getDeprecationStatus());
				setVisible(!isDeprecated && artifact != null &&
						!userService.isFollowedArtifact(MavenArtifactNotifierSession.get().getUser(), artifact));
			}
		};
		add(follow);
		
		// Unfollow
		AjaxLink<Artifact> unfollow = new AjaxLink<Artifact>("unfollow", artifactModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					if (!userService.unfollowArtifact(MavenArtifactNotifierSession.get().getUser(), getModelObject())) {
						getSession().warn(getString("artifact.delete.notFollowed"));
					}
					target.add(getPage());
				} catch (Exception e) {
					LOGGER.error("Error occured while unfollowing artifact", e);
					getSession().error(getString("common.error.unexpected"));
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				Artifact artifact = getModelObject();
				setVisible(artifact != null && userService.isFollowedArtifact(MavenArtifactNotifierSession.get().getUser(), artifact));
			}
		};
		add(unfollow);
		
		add(new DeprecatedArtifactPanel("deprecated", artifactModel));
		
		add(new ArtifactDescriptionPanel("artifactDescriptionPanel", artifactModel));
		add(new FollowedArtifactNotificationRulesPanel("notificationRulesPanel", followedArtifactModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return DashboardPage.class;
	}
	
	private IModel<Artifact> getArtifactModel() {
		return artifactModel;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (artifactModel != null) {
			artifactModel.detach();
		}
		if (followedArtifactModel != null) {
			followedArtifactModel.detach();
		}
	}
}
