package fr.openwide.maven.artifact.notifier.web.application.artifact.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;

import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactDescriptionPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.DeprecatedArtifactPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.FollowedArtifactNotificationRulesPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactDeprecationFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;
import fr.openwide.maven.artifact.notifier.web.application.navigation.page.DashboardPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ArtifactDescriptionPage extends MainTemplate {

	private static final long serialVersionUID = 2693888834363896915L;
	
	private static final String LABEL_CSS_CLASS_DEFAULT = "label";
	
	private static final String LABEL_CSS_CLASS_SUCCESS = "label label-info";
	
	private static final String LABEL_ICON_CLASS_DEFAULT = "icon-remove icon-white";
	
	private static final String LABEL_ICON_CLASS_SUCCESS = "icon-star icon-white";

	@SpringBean
	private IArtifactService artifactService;
	
	@SpringBean
	private IUserService userService;
	
	private IModel<Artifact> artifactModel;
	
	private IModel<FollowedArtifact> followedArtifactModel;
	
	public ArtifactDescriptionPage(PageParameters parameters) {
		super(parameters);
		
		final Artifact artifact = LinkUtils.extractArtifactPageParameter(artifactService, parameters, getApplication().getHomePage());
		artifactModel = new GenericEntityModel<Long, Artifact>(artifact);
		
		FollowedArtifact followedArtifact = userService.getFollowedArtifact(MavenArtifactNotifierSession.get().getUser(), artifact);
		followedArtifactModel = new GenericEntityModel<Long, FollowedArtifact>(followedArtifact);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("dashboard.pageTitle"), DashboardPage.class));
		addBreadCrumbElement(new BreadCrumbElement(new StringResourceModel("artifact.description.pageTitle", artifactModel), getPageClass(), parameters));
		
		add(new Label("pageTitle", new StringResourceModel("artifact.description.pageTitle", artifactModel)));
		
		String key, cssClass, iconClass;
		if (followedArtifact != null) {
			key = "artifact.description.followed";
			cssClass = LABEL_CSS_CLASS_SUCCESS;
			iconClass = LABEL_ICON_CLASS_SUCCESS;
		} else {
			key = "artifact.description.notFollowed";
			cssClass = LABEL_CSS_CLASS_DEFAULT;
			iconClass = LABEL_ICON_CLASS_DEFAULT;
		}
		
		WebMarkupContainer followed = new WebMarkupContainer("followed");
		followed.add(new ClassAttributeAppender(cssClass));
		followed.add(new Label("followedLabel", new ResourceModel(key)));
		
		WebMarkupContainer followedIcon = new WebMarkupContainer("followedIcon");
		followedIcon.add(new ClassAttributeAppender(iconClass));
		followed.add(followedIcon);
		add(followed);

		// Deprecation popup
		ArtifactDeprecationFormPopupPanel deprecationPopup = new ArtifactDeprecationFormPopupPanel("deprecationPopup", artifactModel);
		add(deprecationPopup);
		
		Button deprecate = new Button("deprecation");
		deprecate.add(new AjaxModalOpenBehavior(deprecationPopup, MouseEvent.CLICK) {
			private static final long serialVersionUID = 5414159291353181776L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		deprecate.add(new Label("deprecationLabel", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				if (ArtifactDeprecationStatus.DEPRECATED.equals(artifact.getDeprecationStatus())) {
					return getString("artifact.deprecation.unmarkAsDeprecated");
				}
				return getString("artifact.deprecation.markAsDeprecated");
			}
		}));
		add(deprecate);
		
		add(new DeprecatedArtifactPanel("deprecated", artifactModel));
		
		add(new ArtifactDescriptionPanel("artifactDescriptionPanel", artifactModel));
		add(new FollowedArtifactNotificationRulesPanel("notificationRulesPanel", followedArtifactModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return DashboardPage.class;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (artifactModel != null) {
			artifactModel.detach();
		}
		if (followedArtifactModel != null) {
			followedArtifactModel.detach();
		}
	}
}
