package fr.openwide.maven.artifact.notifier.web.application.administration.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;

import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.ArtifactFollowersPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.template.AdministrationTemplate;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactDescriptionPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.DeprecatedArtifactPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactDeprecationFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class AdministrationArtifactDescriptionPage extends AdministrationTemplate {

	private static final long serialVersionUID = -550100874222819991L;

	@SpringBean
	private IArtifactService artifactService;

	private IModel<Artifact> artifactModel;

	public AdministrationArtifactDescriptionPage(PageParameters parameters) {
		super(parameters);
		
		Artifact artifact = LinkUtils.extractArtifactPageParameter(artifactService, parameters, AdministrationArtifactPortfolioPage.class);
		artifactModel = new GenericEntityModel<Long, Artifact>(artifact);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("navigation.administration.artifact"),
				AdministrationArtifactPortfolioPage.class));
		
		addBreadCrumbElement(new BreadCrumbElement(new StringResourceModel("artifact.description.pageTitle", artifactModel),
				AdministrationArtifactDescriptionPage.class, parameters));
		
		add(new Label("pageTitle", new StringResourceModel("artifact.description.pageTitle", artifactModel)));
		
		// Deprecation popup
		ArtifactDeprecationFormPopupPanel deprecationPopup = new ArtifactDeprecationFormPopupPanel("deprecationPopup", artifactModel);
		add(deprecationPopup);
		
		Button deprecate = new Button("deprecate");
		deprecate.add(new AjaxModalOpenBehavior(deprecationPopup, MouseEvent.CLICK) {
			private static final long serialVersionUID = 5414159291353181776L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		add(deprecate);
		
		add(new DeprecatedArtifactPanel("deprecated", artifactModel));
		
		add(new ArtifactDescriptionPanel("description", artifactModel));
		add(new ArtifactFollowersPanel("followers", artifactModel));
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return AdministrationArtifactPortfolioPage.class;
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		
		if (artifactModel != null) {
			artifactModel.detach();
		}
	}
}
