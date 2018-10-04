package fr.openwide.maven.artifact.notifier.web.application.administration.page;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.sync.service.IMavenSynchronizationService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.AdministrationArtifactSearchPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.ArtifactPortfolioPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.model.ArtifactDataProvider;
import fr.openwide.maven.artifact.notifier.web.application.administration.template.AdministrationTemplate;

public class AdministrationArtifactPortfolioPage extends AdministrationTemplate {

	private static final long serialVersionUID = 1824247169136460059L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationArtifactPortfolioPage.class);

	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;

	@SpringBean
	private IMavenSynchronizationService mavenSynchronizationService;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(AdministrationArtifactPortfolioPage.class)
				.build();
	}
	
	public AdministrationArtifactPortfolioPage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("navigation.administration.artifact"),
				AdministrationArtifactPortfolioPage.linkDescriptor()));
		
		// Synchronize button
		add(new Link<Void>("syncAllBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				final AtomicReference<Exception> exception = new AtomicReference<Exception>(null);
				try {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								mavenSynchronizationService.synchronizeAllArtifactsAndNotifyUsers();
							} catch (ServiceException|SecurityServiceException|InterruptedException e) {
								exception.set(e);
								if (e instanceof InterruptedException) {
									Thread.currentThread().interrupt();
								}
							}
						}
					});
					thread.start();
					thread.join();
				} catch (InterruptedException|RuntimeException e) {
					exception.set(e);
					if (e instanceof InterruptedException) {
						Thread.currentThread().interrupt();
					}
				}
				if (exception.get() != null) {
					getSession().error(getString("administration.artifact.syncAll.error"));
				} else {
					getSession().success(getString("administration.artifact.syncAll.success"));
				}
			}
		});
		
		// Refresh latest versions button
		add(new Link<Void>("refreshAllBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					mavenSynchronizationService.refreshAllLatestVersions();
					getSession().success(getString("administration.artifact.refreshAll.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while refreshing artifacts", e);
					getSession().error(getString("administration.artifact.refreshAll.error"));
				}
			}
		});
		
		IModel<String> searchTermModel = Model.of("");
		IModel<ArtifactDeprecationStatus> deprecationModel = Model.of();
		
		ArtifactPortfolioPanel portfolioPanel = new ArtifactPortfolioPanel("portfolio",
				new ArtifactDataProvider(searchTermModel, deprecationModel), configurer.getPortfolioItemsPerPage());
		add(portfolioPanel);
		
		add(new AdministrationArtifactSearchPanel("searchPanel", portfolioPanel.getPageable(), searchTermModel, deprecationModel));
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return AdministrationArtifactPortfolioPage.class;
	}
}
