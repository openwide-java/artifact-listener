package fr.openwide.maven.artifact.notifier.web.application.artifact.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.PomArtifactPortfolioCheckPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactPomSearchPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;

@AuthorizeInstantiation(CoreAuthorityConstants.ROLE_AUTHENTICATED)
public class ArtifactPomSearchPage extends MainTemplate {

	private static final long serialVersionUID = 2780987980751053482L;

	public ArtifactPomSearchPage(PageParameters parameters) {
		super(parameters);

		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("artifact.follow.search.pom.pageTitle"), getPageClass()));
		add(new Label("pageTitle", new ResourceModel("artifact.follow.search.pom.pageTitle")));
		
		IModel<PomBean> pomBeanModel = Model.of();
		
		PomArtifactPortfolioCheckPanel pomArtifactCheckPanel = new PomArtifactPortfolioCheckPanel("pomArtifactCheckPanel", pomBeanModel);
		add(pomArtifactCheckPanel);
		
		add(new ArtifactPomSearchPanel("pomArtifactSearchPanel", pomArtifactCheckPanel.getDataViews(), pomBeanModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ArtifactSearchPage.class;
	}
}
