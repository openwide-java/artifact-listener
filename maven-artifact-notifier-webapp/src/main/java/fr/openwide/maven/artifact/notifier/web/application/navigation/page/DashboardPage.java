package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.component.DashboardArtifactPortfolioPanel;
import fr.openwide.maven.artifact.notifier.web.application.navigation.component.DashboardNotificationListViewPanel;
import fr.openwide.maven.artifact.notifier.web.application.navigation.model.NotificationsModel;

@AuthorizeInstantiation(CoreAuthorityConstants.ROLE_AUTHENTICATED)
public class DashboardPage extends MainTemplate {

	private static final long serialVersionUID = -6767518941118385548L;

	@SpringBean
	private IUserService userService;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(DashboardPage.class)
				.build();
	}
	
	public DashboardPage(PageParameters parameters) {
		super(parameters);
		
		addBodyCssClass("force-vertical-scroll");
		
		addHeadPageTitleElement(new BreadCrumbElement(new ResourceModel("dashboard.pageTitle")));
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("dashboard.pageTitle"), getPageClass()));
		
		add(new Label("pageTitle", new ResourceModel("dashboard.pageTitle")));
		
		DashboardNotificationListViewPanel notificationsPanel = new DashboardNotificationListViewPanel("notificationPanel",
				new NotificationsModel(MavenArtifactNotifierSession.get().getUserModel()));
		add(notificationsPanel);
		
		DashboardArtifactPortfolioPanel artifactPanel = new DashboardArtifactPortfolioPanel("artifactPanel",
				new LoadableDetachableModel<List<FollowedArtifact>>() {
			private static final long serialVersionUID = -8484961470906264804L;

			@Override
			protected List<FollowedArtifact> load() {
				List<FollowedArtifact> followedArtifacts = userService.listFollowedArtifacts(MavenArtifactNotifierSession.get().getUser());
				Collections.sort(followedArtifacts);
				return followedArtifacts;
			}
		});
		add(artifactPanel);
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return DashboardPage.class;
	}
}
