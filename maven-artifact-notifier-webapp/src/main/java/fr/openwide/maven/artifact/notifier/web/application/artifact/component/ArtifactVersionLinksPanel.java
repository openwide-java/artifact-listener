package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.markup.html.basic.HideableExternalLink;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.common.model.EitherModel;

public class ArtifactVersionLinksPanel extends GenericPanel<ArtifactVersion> {

	private static final long serialVersionUID = 6624055534980328744L;
	
	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	@SpringBean
	private IArtifactService artifactService;

	public ArtifactVersionLinksPanel(String id, IModel<? extends ArtifactVersion> model) {
		super(id, model);
		
		// Changelog link
		IModel<String> versionChangelogUrlModel = new ArtifactVersionAdditionalInformationModel<String>(
				BindingModel.of(getModel(), Binding.artifactVersion().additionalInformation().changelogUrl().url()),
				BindingModel.of(getModel(), Binding.artifactVersion().projectVersion().additionalInformation().changelogUrl().url()));
		IModel<String> changelogUrlModel = new ArtifactVersionAdditionalInformationModel<String>(
				versionChangelogUrlModel,
				BindingModel.of(getModel(), Binding.artifactVersion().projectVersion().project().additionalInformation().changelogUrl().url()));
		add(new HideableExternalLink("changelogLink", changelogUrlModel));
		
		// Release notes link
		IModel<String> releaseNotesUrlModel = new ArtifactVersionAdditionalInformationModel<String>(
				BindingModel.of(getModel(), Binding.artifactVersion().additionalInformation().releaseNotesUrl().url()),
				BindingModel.of(getModel(), Binding.artifactVersion().projectVersion().additionalInformation().releaseNotesUrl().url()));
		add(new HideableExternalLink("releaseNotesLink", releaseNotesUrlModel));

		// Announce link
		IModel<String> announceUrlModel = new ArtifactVersionAdditionalInformationModel<String>(
				BindingModel.of(getModel(), Binding.artifactVersion().additionalInformation().announceUrl().url()),
				BindingModel.of(getModel(), Binding.artifactVersion().projectVersion().additionalInformation().announceUrl().url()));
		add(new HideableExternalLink("announceLink", announceUrlModel));
		
		// Maven central link
		add(new ExternalLink("mavenCentralLink", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				ArtifactVersion artifactVersion = getModelObject();
				return artifactVersion != null ? mavenCentralSearchUrlService.getVersionUrl(artifactVersion) : null;
			}
		}));
	}

	private class ArtifactVersionAdditionalInformationModel<T> extends EitherModel<T> {

		private static final long serialVersionUID = 8116743900640164832L;
		
		private IModel<? extends T> artifactVersionAdditionalInformationModel;

		public ArtifactVersionAdditionalInformationModel(IModel<? extends T> artifactVersionAdditionalInformationModel,
				IModel<? extends T> projectVersionAdditionalInformationModel) {
			super(artifactVersionAdditionalInformationModel, projectVersionAdditionalInformationModel);
			this.artifactVersionAdditionalInformationModel = artifactVersionAdditionalInformationModel;
		}
		
		@Override
		protected boolean shouldGetFirstModel() {
			ArtifactVersion artifactVersion = ArtifactVersionLinksPanel.this.getModelObject();
			return artifactVersion != null ? !artifactService.hasProject(artifactVersion.getArtifact()) ||
					artifactVersionAdditionalInformationModel.getObject() != null : true;
		}
	}
}
