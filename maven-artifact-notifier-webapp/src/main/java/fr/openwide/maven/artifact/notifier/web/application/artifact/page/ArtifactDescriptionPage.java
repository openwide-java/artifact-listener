package fr.openwide.maven.artifact.notifier.web.application.artifact.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.markup.html.basic.CountLabel;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.link.descriptor.parameter.CommonParameters;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactDescriptionPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactProjectPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.DeprecatedArtifactPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.FollowedArtifactNotificationRulesPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.behavior.AuthenticatedOnlyBehavior;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.DashboardPage;

public class ArtifactDescriptionPage extends MainTemplate {

	private static final long serialVersionUID = 2693888834363896915L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactDescriptionPage.class);

	@SpringBean
	private IArtifactService artifactService;
	
	@SpringBean
	private IUserService userService;
	
	private IModel<Artifact> artifactModel;
	
	private IModel<FollowedArtifact> followedArtifactModel;
	
	public static IPageLinkDescriptor linkDescriptor(IModel<Artifact> artifactModel) {
		return new LinkDescriptorBuilder()
				.page(ArtifactDescriptionPage.class)
				.map(CommonParameters.NATURAL_ID, artifactModel, Artifact.class).mandatory()
				.build();
	}
	
	public ArtifactDescriptionPage(PageParameters parameters) {
		super(parameters);
		
		artifactModel = new GenericEntityModel<Long, Artifact>(null);
		
		try {
			linkDescriptor(artifactModel).extract(parameters);
		} catch (Exception e) {
			LOGGER.error("Error on artifact loading", e);
			getSession().error(getString("artifact.error"));
			
			throw ArtifactSearchPage.linkDescriptor().newRestartResponseException();
		}
		
		followedArtifactModel = new LoadableDetachableModel<FollowedArtifact>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected FollowedArtifact load() {
				User user = MavenArtifactNotifierSession.get().getUser();
				if (user != null) {
					return userService.getFollowedArtifact(user, getArtifactModel().getObject());
				}
				return null;
			}
		};
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("dashboard.pageTitle"), DashboardPage.class));
		addBreadCrumbElement(new BreadCrumbElement(new StringResourceModel("artifact.description.pageTitle", artifactModel), getPageClass(), parameters));
		
		add(new Label("pageTitle", new StringResourceModel("artifact.description.pageTitle", artifactModel)));
		
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
				User user = MavenArtifactNotifierSession.get().getUser();
				boolean isDeprecated = artifact == null || ArtifactDeprecationStatus.DEPRECATED.equals(artifact.getDeprecationStatus());
				
				setVisible(!isDeprecated && user != null && !userService.isFollowedArtifact(user, artifact));
			}
		};
		follow.add(new AuthenticatedOnlyBehavior());
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
				User user = MavenArtifactNotifierSession.get().getUser();
				
				setVisible(user != null && artifact != null && userService.isFollowedArtifact(user, artifact));
			}
		};
		unfollow.add(new AuthenticatedOnlyBehavior());
		add(unfollow);
		
		// Followers count label
		add(new CountLabel("followersCountLabel", "artifact.description.followers", new LoadableDetachableModel<Long>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Long load() {
				Artifact artifact = artifactModel.getObject();
				return artifact != null ? artifact.getFollowersCount() : 0;
			}
		}));
		
		add(new DeprecatedArtifactPanel("deprecated", artifactModel));
		
		add(new ArtifactDescriptionPanel("artifactDescriptionPanel", artifactModel));
		add(new FollowedArtifactNotificationRulesPanel("notificationRulesPanel", followedArtifactModel));
		
		add(new ArtifactProjectPanel("artifactProjectPanel", artifactModel));
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
