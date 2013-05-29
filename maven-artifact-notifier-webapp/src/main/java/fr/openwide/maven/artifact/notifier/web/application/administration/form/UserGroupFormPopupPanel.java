package fr.openwide.maven.artifact.notifier.web.application.administration.form;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.maven.artifact.notifier.core.business.authority.MavenArtifactNotifierAuthorityUtils;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserGroupDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.form.FormPanelMode;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.AbstractAjaxModalPopupPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.DelegatedMarkupPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;

public class UserGroupFormPopupPanel extends AbstractAjaxModalPopupPanel<UserGroup> {

	private static final long serialVersionUID = 5369095796078187845L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupFormPopupPanel.class);

	@SpringBean
	private IUserGroupService userGroupService;
	
	@SpringBean
	private MavenArtifactNotifierAuthorityUtils authorityUtils;

	private Form<UserGroup> userGroupForm;

	private FormPanelMode mode;

	public UserGroupFormPopupPanel(String id, IModel<UserGroup> userGroupModel) {
		this(id, userGroupModel, FormPanelMode.EDIT);
	}

	public UserGroupFormPopupPanel(String id) {
		this(id, new GenericEntityModel<Long, UserGroup>(new UserGroup()), FormPanelMode.ADD);
	}

	protected UserGroupFormPopupPanel(String id, IModel<UserGroup> userGroupModel, FormPanelMode mode) {
		super(id, userGroupModel);
		
		this.mode = mode;
	}

	@Override
	protected Component createHeader(String wicketId) {
		if (isAddMode()) {
			return new Label(wicketId, new ResourceModel("administration.usergroup.form.addTitle"));
		} else {
			return new Label(wicketId, new StringResourceModel("administration.usergroup.form.editTitle", getModel()));
		}
	}

	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, UserGroupFormPopupPanel.class);
		
		userGroupForm = new Form<UserGroup>("form", getModel());
		body.add(userGroupForm);
		
		TextField<String> nameField = new RequiredTextField<String>("name", BindingModel.of(userGroupForm.getModel(),
				Binding.userGroup().name()));
		nameField.setLabel(new ResourceModel("administration.usergroup.field.name"));
		userGroupForm.add(nameField);
		
		TextArea<String> descriptionField = new TextArea<String>("description", BindingModel.of(userGroupForm.getModel(),
				Binding.userGroup().description()));
		descriptionField.setLabel(new ResourceModel("administration.usergroup.field.description"));
		userGroupForm.add(descriptionField);
		
		final CheckGroup<Authority> authorityCheckGroup = new CheckGroup<Authority>("authoritiesGroup",
				BindingModel.of(userGroupForm.getModel(), Binding.userGroup().authorities()));
		userGroupForm.add(authorityCheckGroup);
		
		ListView<Authority> authoritiesListView = new ListView<Authority>("authorities",
				Model.ofList(authorityUtils.getPublicAuthorities())) {
			private static final long serialVersionUID = -7557232825932251026L;
			
			@Override
			protected void populateItem(ListItem<Authority> item) {
				Authority authority = item.getModelObject();
				
				Check<Authority> authorityCheck = new Check<Authority>("authorityCheck",
						new GenericEntityModel<Long, Authority>(authority));
				
				authorityCheck.setLabel(new ResourceModel("administration.usergroup.authority." + authority.getName()));
				
				authorityCheckGroup.add(authorityCheck);
				item.add(authorityCheck);
			}
		};
		authorityCheckGroup.add(authoritiesListView);
		
		return body;
	}

	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, UserGroupFormPopupPanel.class);
		
		// Validate button
		AjaxButton validate = new AjaxButton("save", userGroupForm) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				UserGroup userGroup = UserGroupFormPopupPanel.this.getModelObject();
				
				try {
					if (isAddMode()) {
						userGroupService.create(userGroup);
						Session.get().success(getString("administration.usergroup.form.add.success"));
						throw new RestartResponseException(AdministrationUserGroupDescriptionPage.class,
								LinkUtils.getUserGroupPageParameters(userGroup));
					} else {
						userGroupService.update(userGroup);
						Session.get().success(getString("administration.usergroup.form.edit.success"));
					}
					closePopup(target);
					target.add(getPage());
				} catch (RestartResponseException e) {
					throw e;
				} catch (Exception e) {
					if (isAddMode()) {
						LOGGER.error("Error occured while creating user group", e);
						Session.get().error(getString("administration.usergroup.form.add.error"));
					} else {
						LOGGER.error("Error occured while updating user group", e);
						Session.get().error(getString("administration.usergroup.form.edit.error"));
					}
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		};
		Label validateLabel;
		if (isAddMode()) {
			validateLabel = new Label("validateLabel", new ResourceModel("common.action.create"));
		} else {
			validateLabel = new Label("validateLabel", new ResourceModel("common.action.save"));
		}
		validate.add(validateLabel);
		footer.add(validate);
		
		// Cancer button
		AbstractLink cancel = new AbstractLink("cancel") {
			private static final long serialVersionUID = 1L;
		};
		addCancelBehavior(cancel);
		footer.add(cancel);
		
		return footer;
	}

	protected boolean isEditMode() {
		return FormPanelMode.EDIT.equals(mode);
	}

	protected boolean isAddMode() {
		return FormPanelMode.ADD.equals(mode);
	}

	@Override
	public IModel<String> getCssClassNamesModel() {
		return Model.of("modal-usergroup");
	}

	@Override
	protected void onShow(AjaxRequestTarget target) {
		super.onShow(target);
		if (isAddMode()) {
			getModel().setObject(new UserGroup());
		}
	}
}
