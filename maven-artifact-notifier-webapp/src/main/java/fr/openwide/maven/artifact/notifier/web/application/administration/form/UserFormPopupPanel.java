package fr.openwide.maven.artifact.notifier.web.application.administration.form;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.form.FormPanelMode;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.AbstractAjaxModalPopupPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.DelegatedMarkupPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.LocaleDropDownChoice;

public class UserFormPopupPanel extends AbstractAjaxModalPopupPanel<User> {

	private static final long serialVersionUID = -3575009149241618972L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserFormPopupPanel.class);

	@SpringBean
	private IUserService userService;

	private FormPanelMode mode;

	private Form<User> userForm;
	
	private PasswordTextField newPasswordField;
	
	private PasswordTextField confirmPasswordField;

	public UserFormPopupPanel(String id, IModel<User> userModel) {
		this(id, userModel, FormPanelMode.EDIT);
	}

	public UserFormPopupPanel(String id) {
		this(id, new GenericEntityModel<Long, User>(new User()), FormPanelMode.ADD);
	}

	public UserFormPopupPanel(String id, IModel<User> userModel, FormPanelMode mode) {
		super(id, userModel);
		
		this.mode = mode;
	}

	@Override
	protected Component createHeader(String wicketId) {
		if (isAddMode()) {
			return new Label(wicketId, new ResourceModel("administration.user.form.addTitle"));
		} else {
			return new Label(wicketId, new StringResourceModel("administration.user.form.editTitle", getModel()));
		}
	}

	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, UserFormPopupPanel.class);
		
		userForm = new Form<User>("form", getModel());
		body.add(userForm);
		
		TextField<String> emailField = new EmailTextField("email", BindingModel.of(userForm.getModel(),
				Binding.user().email())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(UserFormPopupPanel.this.isAddMode());
			}
		};
		emailField.setLabel(new ResourceModel("administration.user.field.email"));
		emailField.setRequired(isAddMode());
		userForm.add(emailField);
		
		TextField<String> fullNameField = new TextField<String>("fullName", BindingModel.of(userForm.getModel(),
				Binding.user().fullName()));
		fullNameField.setLabel(new ResourceModel("administration.user.field.fullName"));
		userForm.add(fullNameField);
		
		WebMarkupContainer passwordContainer = new WebMarkupContainer("passwordContainer") {
			private static final long serialVersionUID = 2727669661139358058L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(UserFormPopupPanel.this.isAddMode());
			}
		};
		userForm.add(passwordContainer);
		
		CheckBox activeField = new CheckBox("active", BindingModel.of(userForm.getModel(), Binding.user().active()));
		activeField.setLabel(new ResourceModel("administration.user.field.active"));
		passwordContainer.add(activeField);
		
		newPasswordField = new PasswordTextField("newPassword", Model.of(""));
		newPasswordField.setLabel(new ResourceModel("administration.user.field.password"));
		newPasswordField.setRequired(true);
		passwordContainer.add(newPasswordField);
		
		confirmPasswordField = new PasswordTextField("confirmPassword", Model.of(""));
		confirmPasswordField.setLabel(new ResourceModel("administration.user.field.confirmPassword"));
		confirmPasswordField.setRequired(true);
		passwordContainer.add(confirmPasswordField);
		
		LocaleDropDownChoice localeField = new LocaleDropDownChoice("locale", BindingModel.of(userForm.getModel(), Binding.user().locale()));
		localeField.setLabel(new ResourceModel("administration.user.field.locale"));
		userForm.add(localeField);
		
		return body;
	}

	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, UserFormPopupPanel.class);
		
		// Validate button
		AjaxButton validate = new AjaxButton("save", userForm) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				User user = UserFormPopupPanel.this.getModelObject();
				
				try {
					user.setUserName(user.getEmail());
					List<User> usersWithSameName = userService.listByUserName(user.getUserName());
					
					if (isAddMode()) {
						String newPasswordValue = newPasswordField.getModelObject();
						String confirmPasswordValue = confirmPasswordField.getModelObject();
						
						if (newPasswordValue != null && confirmPasswordValue != null) {
							if (confirmPasswordValue.equals(newPasswordValue)) {
								if (newPasswordValue.length() >= User.MIN_PASSWORD_LENGTH &&
										newPasswordValue.length() <= User.MAX_PASSWORD_LENGTH) {
									if (usersWithSameName.isEmpty()) {
										userService.create(user);
										userService.setPasswords(user, newPasswordValue);
										
										getSession().success(getString("administration.user.form.add.success"));
										throw AdministrationUserDescriptionPage
												.linkDescriptor(UserFormPopupPanel.this.getModel())
												.newRestartResponseException();
									} else {
										LOGGER.warn("Username '" + user.getUserName() + "' already used");
										form.error(getString("administration.user.form.userName.notUnique"));
									}
								} else {
									LOGGER.warn("Password does not fit criteria.");
									form.error(getString("administration.user.form.password.malformed"));
								}
							} else {
								LOGGER.warn("Password confirmation does not match.");
								form.error(getString("administration.user.form.password.wrongConfirmation"));
							}
						}
					} else {
						if (usersWithSameName.isEmpty() || (usersWithSameName.size() == 1 &&
								user.getId().equals(usersWithSameName.get(0).getId()))) {
							if (user.getLocale() != null) {
								MavenArtifactNotifierSession.get().setLocale(user.getLocale());
							}
							userService.update(user);
							getSession().success(getString("administration.user.form.edit.success"));
							closePopup(target);
							target.add(getPage());
						} else {
							LOGGER.warn("Username '" + user.getUserName() + "' already used");
							form.error(getString("administration.user.form.userName.notUnique"));
						}
					}
				} catch (RestartResponseException e) {
					throw e;
				} catch (Exception e) {
					if (isAddMode()) {
						LOGGER.error("Error occured while creating user", e);
						Session.get().error(getString("administration.user.form.add.error"));
					} else {
						LOGGER.error("Error occured while updating user", e);
						Session.get().error(getString("administration.user.form.edit.error"));
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
		
		// Cancel button
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
		return Model.of("modal-user-form");
	}

	@Override
	protected void onShow(AjaxRequestTarget target) {
		super.onShow(target);
		if (isAddMode()) {
			getModel().setObject(new User());
		}
	}
}
