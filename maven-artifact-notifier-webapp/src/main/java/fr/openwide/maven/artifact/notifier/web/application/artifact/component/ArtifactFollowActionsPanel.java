package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.behavior.AuthenticatedOnlyBehavior;

public class ArtifactFollowActionsPanel extends GenericPanel<Artifact> {

	private static final long serialVersionUID = -4498682696803901143L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactFollowActionsPanel.class);
	
	@SpringBean
	private IUserService userService;
	
	public ArtifactFollowActionsPanel(String id, IModel<? extends Artifact> model) {
		super(id, model);
		
		// Follow
		AjaxLink<Artifact> follow = new AjaxLink<Artifact>("follow", getModel()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.followArtifact(MavenArtifactNotifierSession.get().getUser(), getModelObject());
					refresh(target);
				} catch (AlreadyFollowedArtifactException e) {
					getSession().warn(getString("artifact.follow.alreadyFollower"));
					refresh(target);
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
		
		// Deprecated
		final IModel<Artifact> relatedArtifactModel = BindingModel.of(getModel(), Binding.artifact().relatedArtifact());
		Link<Void> relatedArtifactLink = ArtifactDescriptionPage.linkDescriptor(relatedArtifactModel).link("deprecated");
		relatedArtifactLink.add(new Behavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(Component component) {
				super.onConfigure(component);
				Artifact artifact = ArtifactFollowActionsPanel.this.getModelObject();
				User user = MavenArtifactNotifierSession.get().getUser();
				boolean isDeprecated = artifact != null && ArtifactDeprecationStatus.DEPRECATED.equals(artifact.getDeprecationStatus());
				
				component.setVisibilityAllowed(isDeprecated && user != null && !userService.isFollowedArtifact(user, artifact));
				component.setEnabled(relatedArtifactModel.getObject() != null);
			}
		});
		relatedArtifactLink.add(new AuthenticatedOnlyBehavior());
		relatedArtifactLink.add(new AttributeModifier("title", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				Artifact relatedArtifact = relatedArtifactModel.getObject();
				StringBuilder sb = new StringBuilder(getString("artifact.description.deprecated"));
				if (relatedArtifact != null) {
					sb.append(" ").append(getString("artifact.deprecation.link.title", relatedArtifactModel));
				}
				return sb.toString();
			}
		}));
		add(relatedArtifactLink);
		
		// Unfollow
		AjaxLink<Artifact> unfollow = new AjaxLink<Artifact>("unfollow", getModel()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					if (!userService.unfollowArtifact(MavenArtifactNotifierSession.get().getUser(), getModelObject())) {
						getSession().warn(getString("artifact.delete.notFollowed"));
					}
					refresh(target);
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
		
		add(new WebMarkupContainer("registerTooltip") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(MavenArtifactNotifierSession.get().getUser() == null);
			}
		});
	}
	
	protected void refresh(AjaxRequestTarget target) {
		target.add(getPage());
	}
}
