package fr.openwide.maven.artifact.notifier.web.application.common.component;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.business.statistics.service.IStatisticService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactVersionTagPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;

public class StatisticsPanel extends Panel {
	
	private static final long serialVersionUID = 8349879446477301375L;
	
	@SpringBean
	private IStatisticService statisticService;
	
	@SpringBean
	private IArtifactService artifactService;
	
	@SpringBean
	private IArtifactVersionService artifactVersionService;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	public StatisticsPanel(String id) {
		super(id);
		
		// Most followed artifacts
		IModel<List<Artifact>> mostFollowedArtifactsModel = new LoadableDetachableModel<List<Artifact>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Artifact> load() {
				return artifactService.listMostFollowedArtifacts(configurer.getMostFollowedArtifactsLimit());
			}
		};
		add(new ListView<Artifact>("mostFollowedArtifacts", mostFollowedArtifactsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Artifact> item) {
				item.add(ArtifactDescriptionPage.linkDescriptor(item.getModel()).link("artifactId")
						.setBody(BindingModel.of(item.getModel(), Binding.artifact().artifactKey().key())));
				
				item.add(new Label("followersCount", BindingModel.of(item.getModel(), Binding.artifact().followersCount())));
			}
		});
		
		// Recent releases
		IModel<List<ArtifactVersion>> recentReleasesModel = new LoadableDetachableModel<List<ArtifactVersion>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ArtifactVersion> load() {
				return artifactVersionService.listRecentReleases(configurer.getRecentReleasesLimit());
			}
		};
		add(new ListView<ArtifactVersion>("recentReleases", recentReleasesModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ArtifactVersion> item) {
				item.add(ArtifactDescriptionPage.linkDescriptor(BindingModel.of(item.getModel(), Binding.artifactVersion().artifact())).link("artifactId")
						.setBody(BindingModel.of(item.getModel(), Binding.artifactVersion().artifact().artifactKey().key())));
				
				item.add(new ArtifactVersionTagPanel("version", BindingModel.of(item.getModel(), Binding.artifactVersion().version())));
			}
		});
	}
}
