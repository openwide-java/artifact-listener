package fr.openwide.maven.artifact.notifier.web.application.administration.form;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.AbstractAjaxModalPopupPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.DelegatedMarkupPanel;

public class ChangePasswordPopupPanel extends AbstractAjaxModalPopupPanel<User> {

	private static final long serialVersionUID = -4580284817084080271L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ChangePasswordPopupPanel.class);

	@SpringBean
	private IUserService userService;

	private Form<?> passwordForm;

	private TextField<String> newPasswordField;

	private TextField<String> confirmPasswordField;

	public ChangePasswordPopupPanel(String id, IModel<User> model) {
		super(id, model);
	}

	@Override
	protected Component createHeader(String wicketId) {
		return new Label(wicketId, new ResourceModel("administration.user.changePassword.title"));
	}

	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, ChangePasswordPopupPanel.class);
		
		passwordForm = new Form<Void>("form");
		body.add(passwordForm);
		
		newPasswordField = new PasswordTextField("newPassword", Model.of(""));
		newPasswordField.setLabel(new ResourceModel("administration.user.field.newPassword"));
		newPasswordField.setRequired(true);
		passwordForm.add(newPasswordField);
		
		confirmPasswordField = new PasswordTextField("confirmPassword", Model.of(""));
		confirmPasswordField.setLabel(new ResourceModel("administration.user.field.confirmPassword"));
		confirmPasswordField.setRequired(true);
		passwordForm.add(confirmPasswordField);
		
		return body;
	}

	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, ChangePasswordPopupPanel.class);
		
		// Validate button
		AjaxButton validate = new AjaxButton("save", passwordForm) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					User user = ChangePasswordPopupPanel.this.getModelObject();
					String newPasswordValue = newPasswordField.getModelObject();
					String confirmPasswordValue = confirmPasswordField.getModelObject();
					
					if (newPasswordValue != null && confirmPasswordValue != null) {
						if (confirmPasswordValue.equals(newPasswordValue)) {
							if (newPasswordValue.length() >= User.MIN_PASSWORD_LENGTH && 
									newPasswordValue.length() <= User.MAX_PASSWORD_LENGTH) {
								userService.setPasswords(user, newPasswordValue);
								
								getSession().success(getString("administration.user.changePassword.success"));
								closePopup(target);
							} else {
								form.error(getString("administration.user.form.password.malformed"));
							}
						} else {
							form.error(getString("administration.user.form.password.wrongConfirmation"));
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error occured while changing password.");
					getSession().error(getString("administration.user.changePassword.error"));
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		};
		footer.add(validate);
		
		// Cancel button
		AbstractLink cancel = new AbstractLink("cancel") {
			private static final long serialVersionUID = 1L;
		};
		addCancelBehavior(cancel);
		footer.add(cancel);
		
		return footer;
	}

	@Override
	public IModel<String> getCssClassNamesModel() {
		return Model.of();
	}

}
