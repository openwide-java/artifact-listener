package fr.openwide.maven.artifact.notifier.web.application.navigation.component;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.core.wicket.markup.html.basic.CountLabel;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.PlaceholderContainer;
import fr.openwide.core.wicket.more.markup.html.collection.GenericEntityCollectionView;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.listfilter.ListFilterBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.listfilter.ListFilterOptions;
import fr.openwide.core.wicket.more.markup.html.template.model.NavigationMenuItem;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactVersionTagPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactPomSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.common.model.EitherModel;

public class DashboardArtifactPortfolioPanel extends GenericPanel<List<FollowedArtifact>> {

	private static final long serialVersionUID = 6030960404037116497L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardArtifactPortfolioPanel.class);

	@SpringBean
	private IUserService userService;

	public DashboardArtifactPortfolioPanel(String id, IModel<List<FollowedArtifact>> artifactListModel) {
		super(id, artifactListModel);
		
		// Dropdown
		BookmarkablePageLink<Void> followArtifactLink = new BookmarkablePageLink<Void>("followArtifactLink", ArtifactSearchPage.class);
		followArtifactLink.add(new ClassAttributeAppender("dropdown-toggle"));
		followArtifactLink.add(new AttributeAppender("data-toggle", "dropdown"));
		followArtifactLink.add(new AttributeModifier("href", "#"));
		followArtifactLink.add(new Label("followArtifactLabel", new ResourceModel("dashboard.artifact.add")));
		
		WebMarkupContainer caret = new WebMarkupContainer("caret");
		followArtifactLink.add(caret);
		add(followArtifactLink);
		
		WebMarkupContainer dropdownMenu = new ListView<NavigationMenuItem>("dropdownMenu", getSearchDropDownItems()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<NavigationMenuItem> subMenuItem) {
				NavigationMenuItem subMenu = subMenuItem.getModelObject();
				
				AbstractLink navLink = subMenu.link("searchLink");
				navLink.add(new Label("searchLabel", subMenu.getLabelModel()));
				
				subMenuItem.add(navLink);
			}
		};
		add(dropdownMenu);
		
		// List-filter
		ListFilterOptions listFilterOptions = new ListFilterOptions();
		listFilterOptions.setItemsSelector(".artifact");
		listFilterOptions.setScanSelector(".artifact-property");
		
		add(new ListFilterBehavior(listFilterOptions));
		
		// Followed artifacts
		GenericEntityCollectionView<FollowedArtifact> artifacts = new GenericEntityCollectionView<FollowedArtifact>("artifacts", artifactListModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final Item<FollowedArtifact> item) {
				final IModel<FollowedArtifact> followedArtifactModel = item.getModel();
				item.setOutputMarkupId(true);
				
				final IModel<ArtifactBean> backupArtifactBeanModel = new Model<ArtifactBean>(null);
				
				item.add(new ClassAttributeAppender(new EitherModel<String>(new Model<String>(null), new Model<String>("error")) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected boolean shouldGetFirstModel() {
						return followedArtifactModel.getObject() != null;
					}
				}));
				
				// GroupId
				item.add(new Label("artifactGroup", new EitherModel<String>(
						BindingModel.of(followedArtifactModel, Binding.followedArtifact().artifact().group().groupId()),
						BindingModel.of(backupArtifactBeanModel, Binding.artifactBean().groupId())) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected boolean shouldGetFirstModel() {
						return followedArtifactModel.getObject() != null;
					}
				}));
				
				// ArtifactId
				Link<Void> artifactIdLink = ArtifactDescriptionPage
						.linkDescriptor(BindingModel.of(followedArtifactModel, Binding.followedArtifact().artifact()))
						.link("artifactIdLink");
				item.add(artifactIdLink);
				artifactIdLink.add(new Label("artifactName", new EitherModel<String>(
						BindingModel.of(followedArtifactModel, Binding.followedArtifact().artifact().artifactId()),
						BindingModel.of(backupArtifactBeanModel, Binding.artifactBean().artifactId())) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected boolean shouldGetFirstModel() {
						return followedArtifactModel.getObject() != null;
					}
				}));
				
				// Rules
				item.add(new CountLabel("rules", "dashboard.artifact.rules",
						BindingModel.of(followedArtifactModel, Binding.followedArtifact().artifactNotificationRules().size())));
				
				// Last version
				final IModel<String> lastVersionModel = new EitherModel<String>(
						BindingModel.of(followedArtifactModel, Binding.followedArtifact().artifact().latestVersion().version()),
						BindingModel.of(backupArtifactBeanModel, Binding.artifactBean().latestVersion())) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected boolean shouldGetFirstModel() {
						return followedArtifactModel.getObject() != null;
					}
				};
				ArtifactVersionTagPanel lastVersion = new ArtifactVersionTagPanel("lastVersion", lastVersionModel, false);
				item.add(lastVersion);
				item.add(new WebMarkupContainer("noVersions") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onConfigure() {
						super.onConfigure();
						setVisible(lastVersionModel.getObject() == null);
					}
					
					@Override
					protected void onDetach() {
						super.onDetach();
						lastVersionModel.detach();
					}
				});
				
				// Follow / unfollow
				AjaxLink<ArtifactBean> follow = new AjaxLink<ArtifactBean>("follow", backupArtifactBeanModel) {
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							FollowedArtifact followedArtifact = userService.followArtifactBean(MavenArtifactNotifierSession.get().getUser(), getModelObject());
							backupArtifactBeanModel.setObject(null);
							followedArtifactModel.setObject(followedArtifact);
							target.add(item);
						} catch (AlreadyFollowedArtifactException e) {
							getSession().warn(getString("artifact.follow.alreadyFollower"));
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
						setVisible(artifactBean != null);
					}
				};
				item.add(follow);
				
				AjaxLink<FollowedArtifact> unfollow = new AjaxLink<FollowedArtifact>("unfollow", followedArtifactModel) {
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							FollowedArtifact followedArtifact = getModelObject();
							if (followedArtifact != null) {
								userService.unfollowArtifact(MavenArtifactNotifierSession.get().getUser(), followedArtifact);
								backupArtifactBeanModel.setObject(new ArtifactBean(followedArtifact));
								followedArtifactModel.setObject(null);
								target.add(item);
							} else {
								getSession().warn(getString("artifact.delete.notFollowed"));
							}
						} catch (Exception e) {
							LOGGER.error("Error occured while unfollowing artifact", e);
							Session.get().error(getString("common.error.unexpected"));
						}
						FeedbackUtils.refreshFeedback(target, getPage());
					}
					
					@Override
					protected void onConfigure() {
						super.onConfigure();
						FollowedArtifact followedArtifact = getModelObject();
						setVisible(followedArtifact != null);
					}
				};
				item.add(unfollow);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(getViewSize() > 0);
			}
		};
		add(artifacts);
		
		add(new PlaceholderContainer("artifactsPlaceholder").component(artifacts));
	}
	
	private List<NavigationMenuItem> getSearchDropDownItems() {
		List<NavigationMenuItem> searchItems = Lists.newArrayListWithCapacity(2);
		searchItems.add(ArtifactPomSearchPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.search.pom")));
		searchItems.add(ArtifactSearchPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.search.mavenCentral")));
		return searchItems;
	}
}
