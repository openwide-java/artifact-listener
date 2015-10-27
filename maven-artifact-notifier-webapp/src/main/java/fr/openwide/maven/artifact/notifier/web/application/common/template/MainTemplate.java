package fr.openwide.maven.artifact.notifier.web.application.common.template;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.core.wicket.more.markup.html.basic.EnclosureBehavior;
import fr.openwide.core.wicket.more.markup.html.feedback.AnimatedGlobalFeedbackPanel;
import fr.openwide.core.wicket.more.markup.html.template.AbstractWebPageTemplate;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.analytics.GoogleAnalyticsBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.collapse.BootstrapCollapseJavaScriptResourceReference;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.dropdown.BootstrapDropdownBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.popover.BootstrapPopoverBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.popover.BootstrapPopoverOptions;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.popover.PopoverPlacement;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.tooltip.BootstrapTooltip;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.tooltip.BootstrapTooltipDocumentBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.scrolltotop.ScrollToTopBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.markup.html.template.model.NavigationMenuItem;
import fr.openwide.core.wicket.more.security.page.LogoutPage;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationArtifactPortfolioPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserGroupPortfolioPage;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserPortfolioPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactPomSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils;
import fr.openwide.maven.artifact.notifier.web.application.auth.pac4j.util.Pac4jAuthenticationUtils.Pac4jClient;
import fr.openwide.maven.artifact.notifier.web.application.common.component.FooterPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.component.IdentificationPopoverPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.component.navigation.MavenArtifactNotifierBodyBreadCrumbPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.styles.StylesLessCssResourceReference;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.DashboardPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ViewProfilePage;
import fr.openwide.maven.artifact.notifier.web.application.project.page.ProjectListPage;

public abstract class MainTemplate extends AbstractWebPageTemplate {

	private static final long serialVersionUID = -1312989780696228848L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;

	private List<String> bodyCssClasses = Lists.newArrayList();
	
	private String googleAuthenticationUrl;
	
	public MainTemplate(PageParameters parameters) {
		super(parameters);
		
		// it is necessary to only generate the Google authentication URI once as it depends on a state parameter
		// generated each time
		setGoogleAuthenticationUrl(Pac4jAuthenticationUtils.getClientRedirectUrl(Pac4jClient.GOOGLE_OAUTH2));
		add(new WebMarkupContainer("googleWarning") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!MavenArtifactNotifierSession.get().isSignedIn());
			}
		});
		
		MarkupContainer htmlRootElement = new TransparentWebMarkupContainer("htmlRootElement");
		htmlRootElement.add(AttributeAppender.append("lang", MavenArtifactNotifierSession.get().getLocale().getLanguage()));
		add(htmlRootElement);
		
		MarkupContainer bodyElement = new TransparentWebMarkupContainer("bodyElement");
		bodyElement.add(new ClassAttributeAppender(new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				StringBuilder cssClassesSb = new StringBuilder();
				for (String cssClasse : bodyCssClasses) {
					cssClassesSb.append(cssClasse).append(" ");
				}
				return cssClassesSb.toString();
			}
		}));
		add(bodyElement);
		
		add(new AnimatedGlobalFeedbackPanel("animatedGlobalFeedbackPanel"));
		
		addHeadPageTitlePrependedElement(new BreadCrumbElement(new ResourceModel("common.rootPageTitle")));
		add(createHeadPageTitle("headPageTitle"));
		
		Link<Void> homePageLink = MavenArtifactNotifierApplication.get().getHomePageLinkDescriptor().link("homePageLink");
		if (HomePage.class.equals(getClass())) {
			homePageLink.setEnabled(false);
		} else {
			homePageLink.add(new AttributeAppender("title", new ResourceModel("navigation.backToHome")));
		}
		add(homePageLink);
		
		// Bread crumb
		add(createBodyBreadCrumb("breadCrumb"));
		
		// Main navigation bar
		add(new ListView<NavigationMenuItem>("mainNav", getMainNav()) {
			private static final long serialVersionUID = -2257358650754295013L;
			
			@Override
			protected void populateItem(ListItem<NavigationMenuItem> item) {
				NavigationMenuItem navItem = item.getModelObject();
				
				AbstractLink navLink = navItem.link("navLink");
				navLink.add(new Label("navLabel", navItem.getLabelModel()));
				
				item.setVisible(navItem.isAccessible());
				if (navItem.isActive(MainTemplate.this.getFirstMenuPage())) {
					item.add(new ClassAttributeAppender("active"));
				}
				
				item.add(navLink);
				
				// Dropdown
				List<NavigationMenuItem> subMenuItems = navItem.getSubMenuItems();
				WebMarkupContainer caret = new WebMarkupContainer("caret");
				navLink.add(caret);
				WebMarkupContainer dropdownMenu = new ListView<NavigationMenuItem>("dropdownMenu", subMenuItems) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void populateItem(ListItem<NavigationMenuItem> subMenuItem) {
						NavigationMenuItem subMenu = subMenuItem.getModelObject();
						
						AbstractLink navLink = subMenu.link("subMenuLink");
						navLink.add(new Label("subMenuLabel", subMenu.getLabelModel()));
						
						subMenuItem.setVisible(subMenu.isAccessible());
						subMenuItem.add(navLink);
					}
				};
				item.add(dropdownMenu);
				if (!subMenuItems.isEmpty()) {
					item.add(new ClassAttributeAppender("dropdown"));
					navLink.add(new ClassAttributeAppender("dropdown-toggle"));
					navLink.add(new AttributeAppender("data-toggle", "dropdown"));
					navLink.add(new AttributeModifier("href", "#"));
					caret.setVisible(true);
					dropdownMenu.setVisible(true);
				} else {
					caret.setVisible(false);
					dropdownMenu.setVisible(false);
				}
			}
		});
		
		// User menu
		IModel<String> userDisplayNameModel = new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				String userDisplayName = null;
				User user = MavenArtifactNotifierSession.get().getUser();
				if (user != null) {
					userDisplayName = user.getDisplayName();
				}
				return userDisplayName;
			}
		};
		WebMarkupContainer userMenuContainer = new WebMarkupContainer("userMenuContainer", userDisplayNameModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(getDefaultModelObject() != null);
			}
		};
		add(userMenuContainer);
		
		Link<Void> viewProfileLink = new BookmarkablePageLink<Void>("viewProfileLink", ViewProfilePage.class);
		viewProfileLink.add(new Label("userDisplayName", userDisplayNameModel));
		userMenuContainer.add(viewProfileLink);
		userMenuContainer.add(new BookmarkablePageLink<Void>("logoutLink", LogoutPage.class));
		
		//	Sign in
		Button signIn = new Button("signIn");
		signIn.add(new EnclosureBehavior().model(Predicates.isNull(), MavenArtifactNotifierSession.get().getUserModel()));
		add(signIn);

		IdentificationPopoverPanel identificationPopoverPanel = new IdentificationPopoverPanel("identificationPopoverPanel",
				getGoogleAuthenticationUrl());
		add(identificationPopoverPanel);
		
		BootstrapPopoverOptions popoverOptions = new BootstrapPopoverOptions();
		popoverOptions.setTitleText(new ResourceModel("navigation.signIn").getObject());
		popoverOptions.setContentComponent(identificationPopoverPanel);
		popoverOptions.setPlacement(PopoverPlacement.BOTTOM);
		popoverOptions.setHtml(true);
		popoverOptions.setContainer(".navbar");
		signIn.add(new BootstrapPopoverBehavior(popoverOptions));
		signIn.add(new ClassAttributeAppender(Model.of("popover-btn")));
		
		// Footer
		add(new FooterPanel("footer"));
		
		// Tooltip
		add(new BootstrapTooltipDocumentBehavior(getBootstrapTooltip()));
		
		// Dropdown
		add(new BootstrapDropdownBehavior());
		
		// Scroll to top
		WebMarkupContainer scrollToTop = new WebMarkupContainer("scrollToTop");
		scrollToTop.add(new ScrollToTopBehavior());
		add(scrollToTop);
		
		// Google Analytics
		add(new GoogleAnalyticsBehavior(configurer.getGoogleAnalyticsTrackingId()));
	}

	protected List<NavigationMenuItem> getMainNav() {
		List<NavigationMenuItem> mainNav = Lists.newArrayList();
		
		mainNav.add(DashboardPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.dashboard")));

		NavigationMenuItem searchMenuItem = ArtifactSearchPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.search"));
		searchMenuItem.addSubMenuItem(ArtifactPomSearchPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.search.pom")));
		searchMenuItem.addSubMenuItem(ArtifactSearchPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.search.mavenCentral")));
		mainNav.add(searchMenuItem);
		
		mainNav.add(ProjectListPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.projects")));
		
		mainNav.add(ViewProfilePage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.viewProfile")));
		
		NavigationMenuItem administrationMenuItem = AdministrationArtifactPortfolioPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.administration"));
		administrationMenuItem.addSubMenuItem(AdministrationArtifactPortfolioPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.administration.artifact")));
		administrationMenuItem.addSubMenuItem(AdministrationUserPortfolioPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.administration.user")));
		administrationMenuItem.addSubMenuItem(AdministrationUserGroupPortfolioPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.administration.usergroup")));
		mainNav.add(administrationMenuItem);
		
		return mainNav;
	}

	protected void addBodyCssClass(String cssClass) {
		bodyCssClasses.add(cssClass);
	}

	public static BootstrapTooltip getBootstrapTooltip() {
		BootstrapTooltip bootstrapTooltip = new BootstrapTooltip();
		bootstrapTooltip.setSelector("[title],[data-original-title]");
		bootstrapTooltip.setAnimation(true);
		bootstrapTooltip.setPlacement(BootstrapTooltip.Placement.BOTTOM);
		bootstrapTooltip.setContainer("body");
		return bootstrapTooltip;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(CssHeaderItem.forReference(StylesLessCssResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(BootstrapCollapseJavaScriptResourceReference.get()));
	}

	@Override
	public String getVariation() {
		return BOOTSTRAP3_VARIATION;
	}

	@Override
	protected Component createBodyBreadCrumb(String wicketId) {
		return new MavenArtifactNotifierBodyBreadCrumbPanel(wicketId, bodyBreadCrumbPrependedElementsModel, breadCrumbElementsModel)
				.setDividerModel(Model.of(""))
				.setTrailingSeparator(true);
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return null;
	}

	protected String getGoogleAuthenticationUrl() {
		return googleAuthenticationUrl;
	}

	protected void setGoogleAuthenticationUrl(String googleAuthenticationUrl) {
		this.googleAuthenticationUrl = googleAuthenticationUrl;
	}
}