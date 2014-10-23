package fr.openwide.maven.artifact.notifier.web.application.navigation.component;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.markup.html.link.EmailLink;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailStatus;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.EmailStatusIcon;

public class UserNotificationManagementPanel extends GenericPanel<User> {

	private static final long serialVersionUID = 1955579250974258074L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationManagementPanel.class);

	@SpringBean
	private IUserService userService;

	private ListView<EmailAddress> emailListView;

	public UserNotificationManagementPanel(String id, IModel<User> userModel) {
		super(id, userModel);
		
		add(new Label("notificationSettings", new ResourceModel("profile.notificationSettings")));
		
		// Notify me
		CheckBox notifyCheck = new CheckBox("notifyMe", BindingModel.of(userModel, Binding.user().notificationAllowed()));
		notifyCheck.setLabel(new ResourceModel("profile.notifyMe"));

		Form<Boolean> form = new Form<Boolean>("form", notifyCheck.getModel());
		form.add(notifyCheck);
		add(form);

		form.add(new AjaxSubmitLink("submitLink", form) {
			private static final long serialVersionUID = 6935376642872117563L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					User user = UserNotificationManagementPanel.this.getModelObject();
					
					userService.update(user);
					getSession().success(getString("administration.user.form.edit.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while updating user.", e);
					getSession().error(getString("administration.user.form.edit.error"));
				}
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
		
		add(new Label("additionalEmails", new ResourceModel("profile.additionalEmails")));
		
		// Email list
		IModel<List<EmailAddress>> emailAddressesModel = new LoadableDetachableModel<List<EmailAddress>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<EmailAddress> load() {
				List<EmailAddress> emailAddresses = userService.listAdditionalEmails(UserNotificationManagementPanel.this.getModelObject());
				Collections.sort(emailAddresses);
				return emailAddresses;
			}
		};
		emailListView = new ListView<EmailAddress>("emails", emailAddressesModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final ListItem<EmailAddress> item) {
				item.add(new EmailLink("emailLink", BindingModel.of(item.getModel(), Binding.emailAddress().email())));
				item.add(new EmailStatusIcon("emailStatus", BindingModel.of(item.getModel(), Binding.emailAddress().status())));
				
				item.add(new AjaxConfirmLink<EmailAddress>("deleteLink", item.getModel(),
						new ResourceModel("profile.deleteEmail.title"),
						new StringResourceModel("profile.deleteEmail.confirmation.text", item.getModel()),
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"), null, false) {
					private static final long serialVersionUID = -5179621361619239269L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							EmailAddress email = item.getModelObject();
							
							if (email.getStatus() == EmailStatus.PENDING_CONFIRM) {
								userService.doDeleteEmailAddress(email);
								getSession().success(getString("profile.doDeleteEmail.success"));
							} else if (email.getStatus() == EmailStatus.VALIDATED) {
								userService.deleteEmailAddress(email);
								getSession().success(getString("profile.deleteEmail.success"));
							}
						} catch (Exception e) {
							LOGGER.error("Error occured while sending delete email notification", e);
							getSession().error(getString("profile.deleteEmail.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
				});
			}
		};
		emailListView.setOutputMarkupId(true);
		add(emailListView);
		
		// Empty list
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(emailListView.size() <= 0);
			}
		});
		
		// Add email form
		IModel<String> emptyEmailModel = Model.of("");
		
		final EmailTextField emailField = new EmailTextField("email", emptyEmailModel);
		emailField.setRequired(true);
		
		final Form<String> addEmailForm = new Form<String>("addEmailForm", emptyEmailModel);
		addEmailForm.add(emailField);
		addEmailForm.add(new AjaxSubmitLink("addEmailLink", addEmailForm) {
			private static final long serialVersionUID = 6935376642872117563L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					User user = UserNotificationManagementPanel.this.getModelObject();
					String emailValue = emailField.getModelObject();
					
					if (emailValue != null) {
						userService.addEmailAddress(user, emailValue);
						getSession().success(getString("profile.addEmail.success"));
					}
				} catch (Exception e) {
					LOGGER.error("Error occured while sending add email notification.");
					getSession().error(getString("profile.addEmail.error"));
				}
				emailField.setModelObject(null);
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
		add(addEmailForm);
	}
}
