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

import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.web.application.auth.model.NormalizedOpenIdAttributes;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.form.RegisterFormPanel;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class RegisterPage extends MainTemplate {

	private static final long serialVersionUID = 289306551107177573L;
	
	public RegisterPage(PageParameters parameters) {
		super(parameters);
		
		if (AuthenticatedWebSession.exists() && AuthenticatedWebSession.get().isSignedIn()) {
			redirect(DashboardPage.class);
		}
		
		HttpServletRequest request = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
		NormalizedOpenIdAttributes attributes = (NormalizedOpenIdAttributes) request.getSession().getAttribute(LinkUtils.OPENID_SESSION_CREDENTIALS);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("register.pageTitle"), getPageClass()));
		
		add(new Label("pageTitle", new ResourceModel("register.pageTitle")));
		
		IModel<User> userModel = new GenericEntityModel<Long, User>(new User());
		userModel.getObject().setActive(false);
		
		if (attributes != null) {
			userModel.getObject().setEmail(attributes.getEmailAddress());
			userModel.getObject().setFullName(attributes.getFullName());
			userModel.getObject().setOpenIdIdentifier(attributes.getOpenIdIdentifier());
		}
		
		add(new RegisterFormPanel("registerFormPanel", userModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return RegisterPage.class;
	}
}
