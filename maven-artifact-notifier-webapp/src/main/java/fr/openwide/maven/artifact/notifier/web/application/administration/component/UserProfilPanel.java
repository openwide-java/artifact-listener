package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import java.util.Locale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.markup.html.link.EmailLink;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.image.BooleanIcon;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.user.model.AuthenticationType;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.form.ChangePasswordPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.form.UserFormPopupPanel;

public class UserProfilPanel extends GenericPanel<User> {

	private static final long serialVersionUID = 8651898170121443991L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserProfilPanel.class);

	@SpringBean
	private IUserService userService;

	public UserProfilPanel(String id, final IModel<User> userModel) {
		super(id, userModel);
		
		// Principal email address
		add(new EmailLink("email", BindingModel.of(userModel, Binding.user().email())));
		
		add(new Label("fullName", BindingModel.of(userModel, Binding.user().fullName())));
		
		add(new BooleanIcon("active", BindingModel.of(userModel, Binding.user().active())));
		
		// Dates
		add(new DateLabel("creationDate", BindingModel.of(userModel, Binding.user().creationDate()),
				DatePattern.SHORT_DATETIME));
		add(new DateLabel("lastLoginDate", BindingModel.of(userModel, Binding.user().lastLoginDate()),
				DatePattern.SHORT_DATETIME));
		add(new DateLabel("lastUpdateDate", BindingModel.of(userModel, Binding.user().lastUpdateDate()),
				DatePattern.SHORT_DATETIME));
		add(new Label("locale", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				Locale locale = BindingModel.of(userModel, Binding.user().locale()).getObject();
				return locale != null ? locale.getDisplayName(MavenArtifactNotifierSession.get().getLocale()) : null;
			}
		}));
		
		// User update popup
		UserFormPopupPanel userUpdatePanel = new UserFormPopupPanel("userUpdatePopupPanel", getModel());
		add(userUpdatePanel);
		
		Button updateUser = new Button("updateUser");
		updateUser.add(new AjaxModalOpenBehavior(userUpdatePanel, MouseEvent.CLICK) {
			private static final long serialVersionUID = 5414159291353181776L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		add(updateUser);
		
		// Change password popup
		ChangePasswordPopupPanel changePasswordPanel = new ChangePasswordPopupPanel("changePasswordPopupPanel", getModel());
		add(changePasswordPanel);
		
		Button changeUserPassword = new Button("changeUserPassword") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				User user = userModel.getObject();
				if (user != null) {
					setVisible(AuthenticationType.LOCAL.equals(user.getAuthenticationType()));
				}
			}
		};
		changeUserPassword.add(new AjaxModalOpenBehavior(changePasswordPanel, MouseEvent.CLICK) {
			private static final long serialVersionUID = -7179264122322968921L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		add(changeUserPassword);
		
		// Enable user link
		add(new Link<User>("enableUser", userModel) {
			private static final long serialVersionUID = 6157423807032594861L;
			
			@Override
			public void onClick() {
				try {
					userService.setActive(getModelObject(), true);
					getSession().success(getString("administration.user.enable.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while enabling user", e);
					getSession().error(getString("common.error"));
				}
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!getModelObject().isActive());
			}
		});
		
		// Disable user link
		IModel<String> confirmationTextModel = new StringResourceModel("administration.user.disable.confirmation.text")
				.setParameters(userModel.getObject().getDisplayName());
		
		add(new AjaxConfirmLink<User>("disableUser", userModel,
				new ResourceModel("administration.user.disable.confirmation.title"),
				confirmationTextModel,
				new ResourceModel("common.confirm"),
				new ResourceModel("common.cancel"),
				null, false) {
			private static final long serialVersionUID = 6157423807032594861L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.setActive(getModelObject(), false);
					getSession().success(getString("administration.user.disable.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while disabling user", e);
					getSession().error(getString("common.error"));
				}
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				User displayedUser = getModelObject();
				User currentUser = MavenArtifactNotifierSession.get().getUser();
				setVisible(!displayedUser.equals(currentUser) && displayedUser.isActive());
			}
		});
	}
}
