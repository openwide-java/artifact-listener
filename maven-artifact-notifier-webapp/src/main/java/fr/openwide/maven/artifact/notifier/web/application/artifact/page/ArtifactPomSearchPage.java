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
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactPomSearchResultsPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactPomSearchPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;

public class ArtifactPomSearchPage extends MainTemplate {

	private static final long serialVersionUID = 2780987980751053482L;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(ArtifactPomSearchPage.class)
				.build();
	}

	public ArtifactPomSearchPage(PageParameters parameters) {
		super(parameters);

		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("artifact.follow.search.pom.pageTitle"), ArtifactPomSearchPage.linkDescriptor()));
		add(new Label("pageTitle", new ResourceModel("artifact.follow.search.pom.pageTitle")));
		
		IModel<PomBean> pomBeanModel = Model.of();
		
		ArtifactPomSearchResultsPanel pomArtifactCheckPanel = new ArtifactPomSearchResultsPanel("pomArtifactCheckPanel", pomBeanModel);
		add(pomArtifactCheckPanel);
		
		add(new ArtifactPomSearchPanel("pomArtifactSearchPanel", pomArtifactCheckPanel.getDataViews(), pomBeanModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ArtifactSearchPage.class;
	}
}
