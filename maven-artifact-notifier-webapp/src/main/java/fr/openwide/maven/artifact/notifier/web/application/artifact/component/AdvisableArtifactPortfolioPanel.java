package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.core.wicket.markup.html.basic.CountLabel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.list.GenericPortfolioPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.ArtifactLastVersionModel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.ArtifactModel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.DateLabelWithPlaceholder;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class AdvisableArtifactPortfolioPanel extends GenericPortfolioPanel<Artifact> {

	private static final long serialVersionUID = 2168203516395191437L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisableArtifactPortfolioPanel.class);
	
	@SpringBean
	private IUserService userService;
	
	@SpringBean
	private IFollowedArtifactService followedArtifactService;
	
	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	public AdvisableArtifactPortfolioPanel(String id, final IDataProvider<Artifact> dataProvider, int itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
		
		add(new Label("title", new ResourceModel("artifact.follow.search.advisable.title")));
	}

	@Override
	protected void addItemColumns(final Item<Artifact> item, IModel<? extends Artifact> itemModel) {
		item.setOutputMarkupId(true);
		
		Artifact artifact = item.getModelObject();
		final IModel<Artifact> artifactModel = new ArtifactModel(Model.of(item.getModelObject().getArtifactKey()));
		final ArtifactLastVersionModel artifactLastVersionModel = new ArtifactLastVersionModel(artifactModel);
		
		item.add(new ClassAttributeAppender(new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				boolean isFollowed = userService.isFollowedArtifact(MavenArtifactNotifierSession.get().getUser(), item.getModelObject());
				boolean isDeprecated = artifactModel.getObject() != null &&
						ArtifactDeprecationStatus.DEPRECATED.equals(artifactModel.getObject().getDeprecationStatus());
				return isFollowed ? "success" : (isDeprecated ? "warning" : null);
			}
		}));
		
		// GroupId column
		item.add(new Label("groupId", BindingModel.of(artifactModel, Binding.artifact().group().groupId())));
		item.add(new ExternalLink("groupLink", mavenCentralSearchUrlService.getGroupUrl(artifact.getGroup().getGroupId())));

		// ArtifactId column
		Link<Artifact> localArtifactLink = new BookmarkablePageLink<Artifact>("localArtifactLink", ArtifactDescriptionPage.class,
				LinkUtils.getArtifactPageParameters(artifactModel.getObject()));
		localArtifactLink.add(new Label("artifactId", BindingModel.of(artifactModel, Binding.artifact().artifactId())));
		item.add(localArtifactLink);
		
		item.add(new ExternalLink("artifactLink", mavenCentralSearchUrlService.getArtifactUrl(artifact.getGroup().getGroupId(), artifact.getArtifactId())));
		
		// LastVersion, lastUpdateDate columns
		item.add(new Label("synchronizationPlannedPlaceholder", new ResourceModel("artifact.follow.synchronizationPlanned")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(artifactModel.getObject() != null && !artifactLastVersionModel.isLastVersionAvailable());
			}
		});
		
		WebMarkupContainer localContainer = new WebMarkupContainer("followedArtifact") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(artifactLastVersionModel.isLastVersionAvailable());
			}
		};
		localContainer.add(new ArtifactVersionTagPanel("latestVersion", Model.of(artifactLastVersionModel.getLastVersion())));
		String latestVersion = (artifact.getLatestVersion() != null ? artifact.getLatestVersion().getVersion() : "");
		localContainer.add(new ExternalLink("versionLink", mavenCentralSearchUrlService.getVersionUrl(artifact.getGroup().getGroupId(),
				artifact.getArtifactId(), latestVersion)));
		
		localContainer.add(new DateLabelWithPlaceholder("lastUpdateDate",
				Model.of(artifactLastVersionModel.getLastVersionUpdateDate()), DatePattern.SHORT_DATE));
		item.add(localContainer);

		// Followers count column
		Label followersCount = new CountLabel("followersCount", "artifact.follow.dataView.followers",
				BindingModel.of(artifactModel, Binding.artifact().followersCount()));
		followersCount.add(new AttributeModifier("class", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				if (artifactModel.getObject() != null && artifactModel.getObject().getFollowersCount() > 0) {
					return "badge";
				}
				return null;
			}
		}));
		item.add(followersCount);
		
		// Follow column
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
				setVisible(artifact != null && !userService.isFollowedArtifact(MavenArtifactNotifierSession.get().getUser(), artifact));
			}
		};
		item.add(follow);
		
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
		item.add(unfollow);
	}

	@Override
	protected boolean hasWritePermissionOn(IModel<? extends Artifact> itemModel) {
		return false;
	}
	
	@Override
	protected void doDeleteItem(IModel<? extends Artifact> itemModel) throws ServiceException, SecurityServiceException {
	}

	@Override
	protected boolean isActionAvailable() {
		return false;
	}

	@Override
	protected boolean isDeleteAvailable() {
		return false;
	}

	@Override
	protected boolean isEditAvailable() {
		return false;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getDataView().getDataProvider().size() != 0);
	}
}
