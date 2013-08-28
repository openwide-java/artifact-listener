package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.ReadOnlyModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationArtifactDescriptionPage;

public class UserArtifactsPanel extends GenericPanel<User> {

	private static final long serialVersionUID = 1955579250974258074L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserArtifactsPanel.class);

	@SpringBean
	private IUserService userService;

	private ListView<FollowedArtifact> artifactListView;

	public UserArtifactsPanel(String id, IModel<User> userModel) {
		super(id, userModel);
		
		// Artifacts list
		artifactListView = new ListView<FollowedArtifact>("artifacts", BindingModel.of(getModel(), Binding.user().followedArtifacts())) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final ListItem<FollowedArtifact> item) {
				Link<Void> artifactLink = AdministrationArtifactDescriptionPage
						.linkDescriptor(BindingModel.of(ReadOnlyModel.of(item.getModelObject()), Binding.followedArtifact().artifact()))
						.link("artifactLink");
				artifactLink.add(new Label("artifactId", BindingModel.of(item.getModel(), Binding.followedArtifact().artifact().artifactId())));
				item.add(artifactLink);
				
				item.add(new Label("groupId", BindingModel.of(item.getModel(), Binding.followedArtifact().artifact().group().groupId())));
				
				IModel<String> confirmationTextModel = new StringResourceModel("artifact.delete.confirmation.text", item.getModel());
				
				item.add(new AjaxConfirmLink<FollowedArtifact>("deleteLink", item.getModel(),
						new ResourceModel("artifact.delete.confirmation.title"),
						confirmationTextModel,
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"), null, false) {
					private static final long serialVersionUID = -5179621361619239269L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							User user = UserArtifactsPanel.this.getModelObject();
							FollowedArtifact followedArtifact = getModelObject();
							
							userService.unfollowArtifact(user, followedArtifact);
							Session.get().success(getString("artifact.delete.success"));
						} catch (Exception e) {
							LOGGER.error("Error occured while unfollowing artifact", e);
							Session.get().error(getString("artifact.delete.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
				});
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
	}
}
