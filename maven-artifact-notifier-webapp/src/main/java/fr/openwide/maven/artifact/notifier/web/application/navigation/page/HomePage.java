package fr.openwide.maven.artifact.notifier.web.application.navigation.page;

import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.parameter.service.IParameterService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.common.component.StatisticsPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.model.FollowingStatsModel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.model.ExternalLinks;

@StatelessComponent
public class HomePage extends MainTemplate {
	
	private static final long serialVersionUID = -6767518941118385548L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	@SpringBean
	private IParameterService parameterService;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(HomePage.class)
				.build();
	}
	
	public HomePage(PageParameters parameters) {
		super(parameters);
		
		// Jumbotron
		// 	>	Baseline
		add(new Label("baselineMainLabel", new StringResourceModel("home.baseline.main", new FollowingStatsModel()))
				.setEscapeModelStrings(false));
		
		add(new DateLabel("lastSyncDateLabel", Model.of(parameterService.getLastSynchronizationDate()), DatePattern.SHORT_DATE));
		
		// Statistics
		add(new StatisticsPanel("statistics"));
		
		// Trademarks
		add(new Label("trademarks", new StringResourceModel("home.trademarks", Model.of(ExternalLinks.get(configurer)), (Object) null)).setEscapeModelStrings(false));
	}
	
	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return HomePage.class;
	}
	
	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return null;
	}
	
}
