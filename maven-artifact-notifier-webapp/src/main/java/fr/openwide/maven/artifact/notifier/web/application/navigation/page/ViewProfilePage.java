package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;
import fr.openwide.core.wicket.more.application.CoreWicketAuthenticatedApplication;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.UserProfilPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.component.UserNotificationManagementPanel;

@AuthorizeInstantiation(CoreAuthorityConstants.ROLE_AUTHENTICATED)
public class ViewProfilePage extends MainTemplate {

	private static final long serialVersionUID = -550100874222819991L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewProfilePage.class);
	
	@SpringBean
	private IUserService userService;

	private IModel<User> userModel;

	public ViewProfilePage(PageParameters parameters) {
		super(parameters);
		
		if (!AuthenticatedWebSession.exists() || MavenArtifactNotifierSession.get().getUser() == null) {
			MavenArtifactNotifierSession.get().error(getString("access.denied"));
			redirect(CoreWicketAuthenticatedApplication.get().getSignInPageClass());
		}
		userModel = MavenArtifactNotifierSession.get().getUserModel();
		
		addBreadCrumbElement(new BreadCrumbElement(new StringResourceModel("profile.pageTitle", userModel), ViewProfilePage.class));
		
		add(new Label("pageTitle", new StringResourceModel("profile.pageTitle", userModel)));
		
		// Confirm delete link
		StringBuilder builder = new StringBuilder().append("<div class=\"alert alert-danger\">")
			.append(getString("profile.unsubscribe.warning"))
			.append("</div>")
			.append(getString("profile.unsubscribe.confirmation.text"));
		
		add(new AjaxConfirmLink<User>(
				"deleteLink",
				userModel,
				new ResourceModel("profile.unsubscribe.confirmation.title"),
				Model.of(builder.toString()),
				new ResourceModel("common.yes"),
				new ResourceModel("common.no"),
				null, true
		) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.delete(userModel.getObject());
					
					getSession().success(getString("profile.unsubscribe.success")); // FIXME: Never displayed
					if (AuthenticatedWebSession.exists()) {
						AuthenticatedWebSession.get().invalidate();
					}
				} catch (Exception e) {
					LOGGER.error("Unknown error occured while deleting user", e);
					getSession().error(getString("profile.unsubscribe.error"));
				}
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
		
		add(new UserProfilPanel("profile", userModel));
		add(new UserNotificationManagementPanel("notificationManagementPanel", userModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ViewProfilePage.class;
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		
		if (userModel != null) {
			userModel.detach();
		}
	}
}
