package fr.openwide.maven.artifact.notifier.web.application.common.template;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.core.wicket.more.markup.html.feedback.AnimatedGlobalFeedbackPanel;
import fr.openwide.core.wicket.more.markup.html.template.AbstractWebPageTemplate;
import fr.openwide.core.wicket.more.markup.html.template.component.BreadCrumbPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.analytics.GoogleAnalyticsBehavior;
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
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationArtifactPortfolioPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactPomSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactSearchPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.FooterPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.component.IdentificationPopoverPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.model.HierarchicalNavigationMenuItem;
import fr.openwide.maven.artifact.notifier.web.application.common.template.styles.StylesLessCssResourceReference;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.DashboardPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.HomePage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.RegisterPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.ViewProfilePage;
import fr.openwide.maven.artifact.notifier.web.application.project.page.ProjectListPage;

public abstract class MainTemplate extends AbstractWebPageTemplate {

	private static final long serialVersionUID = -1312989780696228848L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;

	private List<String> bodyCssClasses = Lists.newArrayList();
	
	public MainTemplate(PageParameters parameters) {
		super(parameters);
		
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
		
		add(new Label("headPageTitle", getHeadPageTitleModel()));
		
		// Bread crumb
		add(new BreadCrumbPanel("breadCrumb", getBreadCrumbElementsModel()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isVisible() {
				return isBreadCrumbDisplayed();
			}
		});
		
		// Main navigation bar
		add(new ListView<HierarchicalNavigationMenuItem>("mainNav", getMainNav()) {
			private static final long serialVersionUID = -2257358650754295013L;
			
			@Override
			protected void populateItem(ListItem<HierarchicalNavigationMenuItem> item) {
				HierarchicalNavigationMenuItem navItem = item.getModelObject();
				
				Link<Void> navLink = navItem.link("navLink");
				navLink.add(new Label("navLabel", navItem.getLabelModel()));
				
				item.setVisible(navItem.isAccessible());
				if (navItem.isActive(MainTemplate.this.getFirstMenuPage())) {
					item.add(new ClassAttributeAppender("active"));
				}
				
				item.add(navLink);
				
				// Dropdown
				List<NavigationMenuItem> sousMenus = navItem.getSubMenuItems();
				WebMarkupContainer caret = new WebMarkupContainer("caret");
				navLink.add(caret);
				WebMarkupContainer dropdownMenu = new ListView<NavigationMenuItem>("dropdownMenu", sousMenus) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void populateItem(ListItem<NavigationMenuItem> subMenuItem) {
						NavigationMenuItem subMenu = subMenuItem.getModelObject();
						
						Link<Void> navLink = subMenu.link("sousMenuLink");
						navLink.add(new Label("sousMenuLabel", subMenu.getLabelModel()));
						
						subMenuItem.setVisible(subMenu.isAccessible());
						subMenuItem.add(navLink);
					}
				};
				item.add(dropdownMenu);
				if (!sousMenus.isEmpty()) {
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
		
		// Second level navigation bar
		add(new ListView<NavigationMenuItem>("subNav", getSubNav()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<NavigationMenuItem> item) {
				NavigationMenuItem navItem = item.getModelObject();
				
				Link<Void> navLink = navItem.link("navLink");
				navLink.add(new Label("navLabel", navItem.getLabelModel()));
				
				item.setVisible(navItem.isAccessible());
				if (navItem.isActive(MainTemplate.this.getSecondMenuPage())) {
					item.add(new ClassAttributeAppender("active"));
				}
				
				item.add(navLink);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				List<NavigationMenuItem> navigationMenuItems = getModelObject();
				setVisible(navigationMenuItems != null && !navigationMenuItems.isEmpty());
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
		
		// Navigation bar right part
		//	>	Register
		WebMarkupContainer registerContainer = new WebMarkupContainer("register") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!AuthenticatedWebSession.exists() || !AuthenticatedWebSession.get().isSignedIn());
			}
		};
		if (RegisterPage.class.equals(getFirstMenuPage())) {
			registerContainer.add(new ClassAttributeAppender("active"));
		}
		add(registerContainer);
		
		BookmarkablePageLink<Void> registerLink = new BookmarkablePageLink<Void>("registerLink", RegisterPage.class);
		registerLink.add(new Label("registerLabel", new ResourceModel("navigation.public.register")));
		registerContainer.add(registerLink);
		
		//	>	Sign in
		Button signIn = new Button("signIn") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!AuthenticatedWebSession.exists() || !AuthenticatedWebSession.get().isSignedIn());
			}
		};
		add(signIn);

		IdentificationPopoverPanel identificationPopoverPanel = new IdentificationPopoverPanel("identificationPopoverPanel");
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

	protected List<HierarchicalNavigationMenuItem> getMainNav() {
		List<HierarchicalNavigationMenuItem> mainNav = Lists.newArrayList();
		
		if (!AuthenticatedWebSession.exists() || !AuthenticatedWebSession.get().isSignedIn()) {
			mainNav.add(new HierarchicalNavigationMenuItem(new ResourceModel("navigation.public.home"), HomePage.class));
//			mainNav.add(new MavenArtifactNotifierNavigationMenuItem(new ResourceModel("navigation.public.register"), RegisterPage.class));
		}
		mainNav.add(new HierarchicalNavigationMenuItem(new ResourceModel("navigation.dashboard"), DashboardPage.class));

		HierarchicalNavigationMenuItem searchMenuItem =
				new HierarchicalNavigationMenuItem(new ResourceModel("navigation.search"), ArtifactSearchPage.class);
		searchMenuItem.addSousMenu(new NavigationMenuItem(new ResourceModel("navigation.search.pom"), ArtifactPomSearchPage.class));
		searchMenuItem.addSousMenu(new NavigationMenuItem(new ResourceModel("navigation.search.mavenCentral"), ArtifactSearchPage.class));
		mainNav.add(searchMenuItem);
		
		mainNav.add(new HierarchicalNavigationMenuItem(new ResourceModel("navigation.projects"), ProjectListPage.class));
		
		mainNav.add(new HierarchicalNavigationMenuItem(new ResourceModel("navigation.viewProfile"), ViewProfilePage.class));
		mainNav.add(new HierarchicalNavigationMenuItem(new ResourceModel("navigation.administration"), AdministrationArtifactPortfolioPage.class));
		
		if (AuthenticatedWebSession.exists() && AuthenticatedWebSession.get().isSignedIn()) {
			mainNav.add(new HierarchicalNavigationMenuItem(new ResourceModel("navigation.public.home"), HomePage.class));
		}
		
		return mainNav;
	}

	protected void addBodyCssClass(String cssClass) {
		bodyCssClasses.add(cssClass);
	}

	protected List<NavigationMenuItem> getSubNav() {
		return Lists.newArrayList();
	}

	@Override
	protected String getRootPageTitleLabelKey() {
		return "common.rootPageTitle";
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
	}

	protected boolean isBreadCrumbDisplayed() {
		List<BreadCrumbElement> breadCrumbElements = getBreadCrumbElementsModel().getObject();
		return breadCrumbElements != null && breadCrumbElements.size() > 1;
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return null;
	}
}