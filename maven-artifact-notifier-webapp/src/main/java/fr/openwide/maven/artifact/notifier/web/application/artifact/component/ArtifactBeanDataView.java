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
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.core.wicket.markup.html.basic.CountLabel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.ArtifactLastVersionModel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.ArtifactModel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactPomSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.DateLabelWithPlaceholder;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ArtifactBeanDataView extends DataView<ArtifactBean> {

	private static final long serialVersionUID = -4100134021146074517L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactBeanDataView.class);
	
	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	@SpringBean
	private IUserService userService;
	
	@SpringBean
	private IFollowedArtifactService followedArtifactService;
	
	protected ArtifactBeanDataView(String id, IDataProvider<ArtifactBean> dataProvider) {
		this(id, dataProvider, Integer.MAX_VALUE);
	}
	
	protected ArtifactBeanDataView(String id, IDataProvider<ArtifactBean> dataProvider, long itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
	}

	@Override
	protected void populateItem(final Item<ArtifactBean> item) {
		item.setOutputMarkupId(true);
		
		ArtifactBean artifactBean = item.getModelObject();

		final ArtifactModel artifactModel = new ArtifactModel(Model.of(item.getModelObject().getArtifactKey()));
		final ArtifactLastVersionModel artifactLastVersionModel = new ArtifactLastVersionModel(artifactModel);
		
		item.add(new ClassAttributeAppender(new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				boolean isFollowed = userService.isFollowedArtifactBean(MavenArtifactNotifierSession.get().getUser(), item.getModelObject());
				boolean isDeprecated = artifactModel.getObject() != null &&
						ArtifactDeprecationStatus.DEPRECATED.equals(artifactModel.getObject().getDeprecationStatus());
				return isFollowed ? "success" : (isDeprecated ? "warning" : null);
			}
		}));
		
		// GroupId column
		item.add(new Label("groupId", new PropertyModel<ArtifactBean>(item.getModel(), "groupId")));
		item.add(new ExternalLink("groupLink", mavenCentralSearchUrlService.getGroupUrl(artifactBean.getGroupId())));

		// ArtifactId column
		item.add(new Label("artifactId", new PropertyModel<ArtifactBean>(item.getModel(), "artifactId")));
		item.add(new ExternalLink("artifactLink", mavenCentralSearchUrlService.getArtifactUrl(artifactBean.getGroupId(), artifactBean.getArtifactId())));
		
		// LastVersion and lastUpdateDate columns
		item.add(new Label("unfollowedArtifactPlaceholder", new ResourceModel("artifact.follow.unfollowedArtifact")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(artifactModel.getObject() == null);
			}
		});
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
		localContainer.add(new ExternalLink("versionLink", mavenCentralSearchUrlService.getVersionUrl(artifactBean.getGroupId(),
				artifactBean.getArtifactId(), artifactBean.getLatestVersion())));
		localContainer.add(new DateLabelWithPlaceholder("lastUpdateDate", Model.of(artifactLastVersionModel.getLastVersionUpdateDate()), DatePattern.SHORT_DATE));
		item.add(localContainer);

		// Followers count column
		item.add(new CountLabel("followersCount", "artifact.follow.dataView.followers",
				BindingModel.of(artifactModel, Binding.artifact().followersCount())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (artifactModel.getObject() != null && artifactModel.getObject().getFollowersCount() > 0) {
					add(new AttributeModifier("class", "badge"));
				} else {
					add(new AttributeModifier("class", ""));
				}
			}
		});
		
		// Follow column
		AjaxLink<ArtifactBean> follow = new AjaxLink<ArtifactBean>("follow", item.getModel()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.followArtifactBean(MavenArtifactNotifierSession.get().getUser(), getModelObject());
					refresh(target, item);
				} catch (AlreadyFollowedArtifactException e) {
					getSession().warn(getString("artifact.follow.alreadyFollower"));
					refresh(target, item);
				} catch (Exception e) {
					LOGGER.error("Error occured while following artifact", e);
					getSession().error(getString("common.error.unexpected"));
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				ArtifactBean artifactBean = getModelObject();
				Artifact artifact = artifactModel.getObject();
				boolean isDeprecated = artifact != null && ArtifactDeprecationStatus.DEPRECATED.equals(artifact.getDeprecationStatus());
				setVisible(!isDeprecated && artifactBean != null &&
						!userService.isFollowedArtifactBean(MavenArtifactNotifierSession.get().getUser(), artifactBean));
			}
		};
		item.add(follow);
		
		final IModel<Artifact> relatedArtifactModel = BindingModel.of(artifactModel, Binding.artifact().relatedArtifact());
		Link<Artifact> relatedArtifactLink = new BookmarkablePageLink<Artifact>("relatedArtifactLink",
				ArtifactDescriptionPage.class, LinkUtils.getArtifactPageParameters(relatedArtifactModel.getObject())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				ArtifactBean artifactBean = item.getModelObject();
				Artifact artifact = artifactModel.getObject();
				boolean isDeprecated = artifact != null && ArtifactDeprecationStatus.DEPRECATED.equals(artifact.getDeprecationStatus());
				setVisible(isDeprecated && artifactBean != null &&
						!userService.isFollowedArtifactBean(MavenArtifactNotifierSession.get().getUser(), artifactBean));
				setEnabled(relatedArtifactModel.getObject() != null);
			}
		};
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
		item.add(relatedArtifactLink);
		
		AjaxLink<Artifact> unfollow = new AjaxLink<Artifact>("unfollow", artifactModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					if (!userService.unfollowArtifact(MavenArtifactNotifierSession.get().getUser(), getModelObject())) {
						getSession().warn(getString("artifact.delete.notFollowed"));
					}
					refresh(target, item);
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
	
	protected void refresh(AjaxRequestTarget target, Item<ArtifactBean> item) {
		if (getPage().getPageClass().equals(ArtifactPomSearchPage.class)) {
			target.add(item);
		} else {
			target.add(getPage());
		}
	}
	
	@Override
	protected void onConfigure() {
		setVisible(getDataProvider().size() != 0);
	}
}
