package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.core.wicket.more.model.ReadOnlyModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserGroupDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.UserGroupAutocompleteAjaxComponent;

public class UserMembershipsPanel extends GenericPanel<User> {

	private static final long serialVersionUID = -517286662347263793L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserMembershipsPanel.class);

	@SpringBean
	private IUserGroupService userGroupService;
	
	private ListView<UserGroup> userGroupListView;

	public UserMembershipsPanel(String id, IModel<User> userModel) {
		super(id, userModel);
		
		// Groups list
		userGroupListView = new ListView<UserGroup>("groups", BindingModel.of(getModel(), Binding.user().userGroups())) {
			private static final long serialVersionUID = -6489746843440088695L;
			
			@Override
			protected void populateItem(final ListItem<UserGroup> item) {
				Link<Void> groupLink = AdministrationUserGroupDescriptionPage
						.linkDescriptor(ReadOnlyModel.of(item.getModelObject()))
						.link("groupLink");
				groupLink.add(new Label("name", BindingModel.of(item.getModel(), Binding.userGroup().name())));
				item.add(groupLink);
				
				IModel<String> confirmationTextModel = new StringResourceModel(
						"administration.usergroup.members.delete.confirmation.text",
						null, new Object[] {
								UserMembershipsPanel.this.getModelObject().getDisplayName(),
								item.getModelObject().getName()
						}
				);
				
				item.add(new AjaxConfirmLink<UserGroup>("deleteLink", item.getModel(),
						new ResourceModel("administration.usergroup.members.delete.confirmation.title"),
						confirmationTextModel,
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"),
						null, false) {
					private static final long serialVersionUID = -5179621361619239269L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							UserGroup userGroup = getModelObject();
							User user = UserMembershipsPanel.this.getModelObject();
							
							userGroupService.removePerson(userGroup, user);
							Session.get().success(getString("administration.usergroup.members.delete.success"));
						} catch (Exception e) {
							LOGGER.error("Error occured while removing user from user group", e);
							Session.get().error(getString("administration.usergroup.members.delete.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
				});
			}
		};
		add(userGroupListView);
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = -784607577583169098L;
			
			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(userGroupListView.size() <= 0);
			}
		});
		
		// Add group form
		IModel<UserGroup> emptyUserGroupModel = new GenericEntityModel<Long, UserGroup>(null);
		
		final UserGroupAutocompleteAjaxComponent userGroupAutocomplete = new UserGroupAutocompleteAjaxComponent(
				"userGroupAutocomplete", emptyUserGroupModel);
		userGroupAutocomplete.setAutoUpdate(true);
		
		final Form<UserGroup> addGroupForm = new Form<UserGroup>("addGroupForm", emptyUserGroupModel);
		addGroupForm.add(userGroupAutocomplete);
		addGroupForm.add(new AjaxSubmitLink("addGroupLink", addGroupForm) {
			private static final long serialVersionUID = 6935376642872117563L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				User user = UserMembershipsPanel.this.getModelObject();
				UserGroup selectedUserGroup = userGroupAutocomplete.getModelObject();
				
				if (selectedUserGroup != null) {
					if (!selectedUserGroup.getPersons().contains(user)) {
						try {
							userGroupService.addPerson(selectedUserGroup, user);
							getSession().success(getString("administration.usergroup.members.add.success"));
						} catch (Exception e) {
							LOGGER.error("Unknown error occured while adding a user to a usergroup", e);
							getSession().error(getString("administration.usergroup.members.add.error"));
						}
					} else {
						LOGGER.error("User already added to this group");
						getSession().warn(getString("administration.usergroup.members.add.alreadyMember"));
					}
				}
				userGroupAutocomplete.setModelObject(null);
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
		add(addGroupForm);
	}
}
