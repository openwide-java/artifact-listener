package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.form.RegisterFormPanel;

public class RegisterPage extends MainTemplate {

	private static final long serialVersionUID = 289306551107177573L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterPage.class);
	
	@SpringBean
	private IUserService userService;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(RegisterPage.class)
				.build();
	}
	
	public RegisterPage(PageParameters parameters) {
		super(parameters);
		
		if (AuthenticatedWebSession.exists() && AuthenticatedWebSession.get().isSignedIn()) {
			redirect(DashboardPage.class);
			return;
		}
		
		HttpServletRequest request = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
		ClientAuthenticationToken token = (ClientAuthenticationToken) request.getSession().getAttribute(Pac4jAuthenticationUtils.AUTH_TOKEN_ATTRIBUTE);
		
		IModel<User> userModel = new GenericEntityModel<Long, User>(new User());
		userModel.getObject().setActive(false);
		
		if (token != null && token.getUserProfile() != null) {
			CommonProfile profile = (CommonProfile) token.getUserProfile();
			if (profile.getEmail() != null) {
				User user = userService.getByUserName(profile.getEmail());
				if (user != null) {
					LOGGER.warn("This email address is already used by another user");
					getSession().warn(getString("register.userName.notUnique"));
				}
			}
			
			userModel.getObject().setEmail(profile.getEmail());
			userModel.getObject().setFullName(profile.getDisplayName());
			userModel.getObject().setRemoteIdentifier(profile.getId());
		}

		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("register.pageTitle"), getPageClass()));
		
		add(new Label("pageTitle", new ResourceModel("register.pageTitle")));
		
		add(new RegisterFormPanel("registerFormPanel", userModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return RegisterPage.class;
	}
}
