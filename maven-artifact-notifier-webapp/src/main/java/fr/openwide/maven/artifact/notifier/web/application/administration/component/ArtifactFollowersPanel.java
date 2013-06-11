package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.user.exception.AlreadyFollowedArtifactException;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.UserAutocompleteAjaxComponent;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ArtifactFollowersPanel extends GenericPanel<Artifact> {

	private static final long serialVersionUID = 1955579250974258074L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactFollowersPanel.class);

	@SpringBean
	private IFollowedArtifactService followedArtifactService;
	
	@SpringBean
	private IUserService userService;

	private ListView<User> followersListView;

	public ArtifactFollowersPanel(String id, IModel<Artifact> artifactModel) {
		super(id, artifactModel);
		
		// Followers list
		final IModel<List<User>> followersModel = new LoadableDetachableModel<List<User>>() {
			private static final long serialVersionUID = -8484961470906264804L;

			@Override
			protected List<User> load() {
				List<User> followers = followedArtifactService.listFollowers(getModel().getObject());
				Collections.sort(followers);
				return followers;
			}
		};
		followersListView = new ListView<User>("followers", followersModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final ListItem<User> item) {
				BookmarkablePageLink<User> userLink = new BookmarkablePageLink<User>(
						"userLink",
						AdministrationUserDescriptionPage.class, 
						LinkUtils.getUserPageParameters(item.getModelObject())
				);
				userLink.add(new Label("userName", BindingModel.of(item.getModel(), Binding.user().userName())));
				item.add(userLink);
				
				item.add(new Label("fullName", BindingModel.of(item.getModel(), Binding.user().fullName())));
				
				IModel<String> confirmationTextModel = new StringResourceModel(
						"administration.artifact.followers.delete.confirmation.text",
						null, new Object[] {
								item.getModelObject().getDisplayName(),
								ArtifactFollowersPanel.this.getModelObject().getGroup().getGroupId(),
								ArtifactFollowersPanel.this.getModelObject().getArtifactId()
						}
				);
				
				item.add(new AjaxConfirmLink<User>("deleteLink", item.getModel(),
						new ResourceModel("administration.artifact.followers.delete.confirmation.title"),
						confirmationTextModel,
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"), null, false) {
					private static final long serialVersionUID = -5179621361619239269L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							Artifact artifact = ArtifactFollowersPanel.this.getModelObject();
							User user = getModelObject();
							
							if (userService.unfollowArtifact(user, artifact)) {
								Session.get().success(getString("administration.artifact.followers.delete.success"));
							} else {
								getSession().warn(getString("artifact.delete.notFollowed"));
							}
						} catch (Exception e) {
							LOGGER.error("Error occured while unfollowing artifact", e);
							Session.get().error(getString("administration.artifact.followers.delete.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
				});
			}
		};
		add(followersListView);
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(followersListView.size() <= 0);
			}
		});
		
		// Add member form
		IModel<User> emptyUserModel = new GenericEntityModel<Long, User>(null);
		
		final UserAutocompleteAjaxComponent userAutocomplete = new UserAutocompleteAjaxComponent("userAutocomplete",
				emptyUserModel);
		userAutocomplete.setAutoUpdate(true);
		
		final Form<User> addMemberForm = new Form<User>("addFollowerForm", emptyUserModel);
		addMemberForm.add(userAutocomplete);
		addMemberForm.add(new AjaxSubmitLink("addFollowerLink", addMemberForm) {
			private static final long serialVersionUID = 6935376642872117563L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Artifact artifact = ArtifactFollowersPanel.this.getModelObject();
				User selectedUser = userAutocomplete.getModelObject();
				
				if (selectedUser != null) {
					try {
						userService.followArtifact(selectedUser, artifact);
						getSession().success(getString("administration.artifact.followers.add.success"));
					} catch (AlreadyFollowedArtifactException e) {
						getSession().warn(getString("administration.artifact.followers.add.alreadyFollower"));
					} catch (Exception e) {
						LOGGER.error("Unknown error occured while following an artifact", e);
						getSession().error(getString("administration.artifact.followers.add.error"));
					}
				}
				userAutocomplete.setModelObject(null);
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
		add(addMemberForm);
	}
}
