package fr.openwide.maven.artifact.notifier.web.application.artifact.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactSearchResultsPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.RecommendedArtifactPortfolioPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactSearchPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.ArtifactBeanDataProvider;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.RecommendedArtifactDataProvider;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ArtifactSearchPage extends MainTemplate {

	private static final long serialVersionUID = 2780987980751053482L;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(ArtifactSearchPage.class)
				.build();
	}

	public ArtifactSearchPage(PageParameters parameters) {
		super(parameters);

		String term = parameters.get(LinkUtils.SEARCH_TERM_PARAMETER).toString();
		long pageNumber = LinkUtils.extractPageNumberParameter(parameters);
		
		IModel<String> globalSearchModel = new Model<String>(term);
		IModel<String> searchGroupModel = new Model<String>();
		IModel<String> searchArtifactModel = new Model<String>();
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("artifact.follow.search.pageTitle"), ArtifactSearchPage.linkDescriptor()));
		add(new Label("pageTitle", new ResourceModel("artifact.follow.search.pageTitle")));
		
		add(new RecommendedArtifactPortfolioPanel("recommendedArtifacts",
				new RecommendedArtifactDataProvider(globalSearchModel, searchGroupModel, searchArtifactModel), Integer.MAX_VALUE));
		
		ArtifactSearchResultsPanel artifactSearchResultsPanel = new ArtifactSearchResultsPanel("artifactSearchResultsPanel",
				new ArtifactBeanDataProvider(globalSearchModel, searchGroupModel, searchArtifactModel));
		artifactSearchResultsPanel.getDataView().setCurrentPage(pageNumber);
		add(artifactSearchResultsPanel);
		
		add(new ArtifactSearchPanel("artifactSearchPanel", artifactSearchResultsPanel.getDataView(), globalSearchModel,
				searchGroupModel, searchArtifactModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ArtifactSearchPage.class;
	}
	
	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return ArtifactSearchPage.class;
	}
}
