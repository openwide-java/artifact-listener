package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.EnumLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;

import fr.openwide.core.wicket.markup.html.basic.HideableExternalLink;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.CollectionToListWrapperModel;
import fr.openwide.core.wicket.more.model.ReadOnlyModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactDeprecationFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.AuthenticatedOnlyButton;
import fr.openwide.maven.artifact.notifier.web.application.common.model.EitherModel;

public class ArtifactDescriptionPanel extends GenericPanel<Artifact> {

	private static final long serialVersionUID = 7757299234352613717L;

	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	@SpringBean
	private IArtifactService artifactService;
	
//	private ArtifactVersionFormPopupPanel artifactVersionPopup;
	
	public ArtifactDescriptionPanel(String id, IModel<? extends Artifact> artifactModel) {
		super(id, artifactModel);

		// GroupID
		add(new Label("groupId", BindingModel.of(getModel(), Binding.artifact().group().groupId())));
		add(new ExternalLink("groupLink", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				Artifact artifact = ArtifactDescriptionPanel.this.getModelObject();
				return mavenCentralSearchUrlService.getGroupUrl(artifact.getGroup().getGroupId());
			}
		}));
		
		// ArtifactID
		add(new Label("artifactId", BindingModel.of(getModel(), Binding.artifact().artifactId())));
		add(new ExternalLink("artifactLink", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				Artifact artifact = ArtifactDescriptionPanel.this.getModelObject();
				return mavenCentralSearchUrlService.getArtifactUrl(artifact.getGroup().getGroupId(), artifact.getArtifactId());
			}
		}));
		
		// Deprecation popup
		ArtifactDeprecationFormPopupPanel deprecationPopup = new ArtifactDeprecationFormPopupPanel("deprecationPopup", artifactModel);
		add(deprecationPopup);
		
		// Deprecation status
		add(new EnumLabel<ArtifactDeprecationStatus>("deprecationStatus", BindingModel.of(getModel(), Binding.artifact().deprecationStatus())));
		add(new AuthenticatedOnlyButton("editDeprecation").add(new AjaxModalOpenBehavior(deprecationPopup, MouseEvent.CLICK)));
		
		// Deprecates
		IModel<List<Artifact>> relatedDeprecatedArtifactsModel = new LoadableDetachableModel<List<Artifact>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Artifact> load() {
				return artifactService.listRelatedDeprecatedArtifacts(getModelObject());
			}
		};
		add(new ListView<Artifact>("deprecates", relatedDeprecatedArtifactsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Artifact> item) {
				Link<Void> deprecatedArtifactLink = ArtifactDescriptionPage
						.linkDescriptor(ReadOnlyModel.of(item.getModelObject()))
						.link("deprecatedArtifactLink");
				deprecatedArtifactLink.add(new Label("deprecatedArtifact", BindingModel.of(item.getModel(), Binding.artifact().artifactKey().key())));
				item.add(deprecatedArtifactLink);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!getModelObject().isEmpty());
			}
		});
		
		// Artifact version edit popup
		// XXX: This action is disabled for now, it may be reused in future releases.
//		artifactVersionPopup = new ArtifactVersionFormPopupPanel("artifactVersionPopup");
//		add(artifactVersionPopup);
		
		// Versions
		IModel<Set<ArtifactVersion>> setModel = BindingModel.of(getModel(), Binding.artifact().versions());
		add(new ListView<ArtifactVersion>("artifactVersions", CollectionToListWrapperModel.of(setModel)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ArtifactVersion> item) {
				item.add(new ArtifactVersionTagPanel("version", BindingModel.of(item.getModel(), Binding.artifactVersion().version())));
				item.add(new DateLabel("lastUpdateDate", BindingModel.of(item.getModel(), Binding.artifactVersion().lastUpdateDate()),
						DatePattern.SHORT_DATE));
				
				// Changeloglink
				IModel<String> changelogUrlModel = new ArtifactVersionAdditionalInformationModel<String>(
						BindingModel.of(item.getModel(), Binding.artifactVersion().additionalInformation().changelogUrl().url()),
						BindingModel.of(item.getModel(), Binding.artifactVersion().projectVersion().additionalInformation().changelogUrl().url()));
				item.add(new HideableExternalLink("changelogLink", changelogUrlModel));
				
				// Release notes link
				IModel<String> releaseNotesUrlModel = new ArtifactVersionAdditionalInformationModel<String>(
						BindingModel.of(item.getModel(), Binding.artifactVersion().additionalInformation().releaseNotesUrl().url()),
						BindingModel.of(item.getModel(), Binding.artifactVersion().projectVersion().additionalInformation().releaseNotesUrl().url()));
				item.add(new HideableExternalLink("releaseNotesLink", releaseNotesUrlModel));

				// Announce link
				IModel<String> announceUrlModel = new ArtifactVersionAdditionalInformationModel<String>(
						BindingModel.of(item.getModel(), Binding.artifactVersion().additionalInformation().announceUrl().url()),
						BindingModel.of(item.getModel(), Binding.artifactVersion().projectVersion().additionalInformation().announceUrl().url()));
				item.add(new HideableExternalLink("announceLink", announceUrlModel));
				
				// Maven central link
				item.add(new ExternalLink("mavenCentralLink", mavenCentralSearchUrlService.getVersionUrl(item.getModelObject())));
				
				// Edit action
				// XXX: This action is disabled for now, it may be reused in future releases.
//				Button editButton = new AuthenticatedOnlyButton("edit") {
//					private static final long serialVersionUID = 1L;
//
//					@Override
//					protected void onConfigure() {
//						super.onConfigure();
//						setVisible(!artifactService.hasProject(ArtifactDescriptionPanel.this.getModelObject()));
//					}
//				};
//				editButton.add(new AjaxModalOpenBehavior(artifactVersionPopup, MouseEvent.CLICK) {
//					private static final long serialVersionUID = 1L;
//
//					@Override
//					protected void onShow(AjaxRequestTarget target) {
//						super.onShow(target);
//						artifactVersionPopup.getModel().setObject(item.getModelObject());
//					}
//				});
//				item.add(editButton);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!getModelObject().isEmpty());
			}
		});
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;
			
			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(getModelObject().getVersions().isEmpty());
			}
		});
	}
	
	private class ArtifactVersionAdditionalInformationModel<T> extends EitherModel<T> {

		private static final long serialVersionUID = 8116743900640164832L;

		public ArtifactVersionAdditionalInformationModel(IModel<? extends T> artifactVersionAdditionalInformationModel,
				IModel<? extends T> projectVersionAdditionalInformationModel) {
			super(artifactVersionAdditionalInformationModel, projectVersionAdditionalInformationModel);
		}
		
		@Override
		protected boolean shouldGetFirstModel() {
			return !artifactService.hasProject(ArtifactDescriptionPanel.this.getModelObject());
		}
	}
}
