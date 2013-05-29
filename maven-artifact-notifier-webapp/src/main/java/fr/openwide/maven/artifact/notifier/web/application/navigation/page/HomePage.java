package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.markup.html.basic.EnclosureContainer;
import fr.openwide.core.wicket.more.markup.html.feedback.AnimatedGlobalFeedbackPanel;
import fr.openwide.core.wicket.more.markup.html.template.AbstractWebPageTemplate;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.analytics.GoogleAnalyticsBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.modal.Modal;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.modal.behavior.ModalDiaporamaBehavior;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.common.component.FooterPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.model.FollowingStatsModel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.styles.HomeLessCssResourceReference;
import fr.openwide.maven.artifact.notifier.web.application.navigation.component.HomeIdentificationPanel;
import fr.openwide.maven.artifact.notifier.web.application.navigation.model.ExternalLinks;

@StatelessComponent
public class HomePage extends AbstractWebPageTemplate {
	
	private static final long serialVersionUID = -6767518941118385548L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	public HomePage(PageParameters parameters) {
		super(parameters);
		
		MarkupContainer htmlRootElement = new TransparentWebMarkupContainer("htmlRootElement");
		htmlRootElement.add(AttributeAppender.append("lang", MavenArtifactNotifierSession.get().getLocale().getLanguage()));
		add(htmlRootElement);
		
		add(new Label("headPageTitle", getHeadPageTitleModel()));
		
		add(new AnimatedGlobalFeedbackPanel("animatedGlobalFeedbackPanel"));
		
		// Jumbotron
		// 	>	Baseline
		add(new Label("baselineMainLabel", new StringResourceModel("home.baseline.main", new FollowingStatsModel()))
				.setEscapeModelStrings(false));
		
		// 	>	Subscribe form
		HomeIdentificationPanel identificationPanel = new HomeIdentificationPanel("identificationPanel");
		add(identificationPanel);
		EnclosureContainer identificationEnclosure = new EnclosureContainer("identificationEnclosure").component(identificationPanel);
		add(identificationEnclosure);
		
		identificationEnclosure.setOutputMarkupId(true);
		identificationEnclosure.add(new ModalDiaporamaBehavior("a[data-diaporama=\"screenshot\"]", new Modal()));
		
		// 	>	Dashboard link
		add(new BookmarkablePageLink<Void>("dashboardLink", DashboardPage.class) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(AuthenticatedWebSession.exists() && AuthenticatedWebSession.get().isSignedIn());
			}
		});
		
		// Trademarks
		add(new Label("trademarks", new StringResourceModel("home.trademarks", Model.of(ExternalLinks.get(configurer)), (Object) null)).setEscapeModelStrings(false));
		
		// Footer
		add(new FooterPanel("footer"));
		
		// Google Analytics
		add(new GoogleAnalyticsBehavior(configurer.getGoogleAnalyticsTrackingId()));
	}
	
	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return HomePage.class;
	}
	
	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return null;
	}
	
	@Override
	protected String getRootPageTitleLabelKey() {
		return "common.rootPageTitle";
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(CssHeaderItem.forReference(HomeLessCssResourceReference.get()));
	}
}
