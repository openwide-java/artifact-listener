package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.Set;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.CollectionToListWrapperModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchUrlService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

public class ArtifactDescriptionPanel extends GenericPanel<Artifact> {

	private static final long serialVersionUID = 7757299234352613717L;

	@SpringBean
	private IMavenCentralSearchUrlService mavenCentralSearchUrlService;
	
	public ArtifactDescriptionPanel(String id, IModel<? extends Artifact> artifactModel) {
		super(id, artifactModel);

		add(new Label("groupId", BindingModel.of(getModel(), Binding.artifact().group().groupId())));
		add(new ExternalLink("groupLink", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				Artifact artifact = ArtifactDescriptionPanel.this.getModelObject();
				return mavenCentralSearchUrlService.getGroupUrl(artifact.getGroup().getGroupId());
			}
		}));
		
		add(new Label("artifactId", BindingModel.of(getModel(), Binding.artifact().artifactId())));
		add(new ExternalLink("artifactLink", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				Artifact artifact = ArtifactDescriptionPanel.this.getModelObject();
				return mavenCentralSearchUrlService.getArtifactUrl(artifact.getGroup().getGroupId(), artifact.getArtifactId());
			}
		}));
		
		IModel<Set<ArtifactVersion>> setModel = BindingModel.of(getModel(), Binding.artifact().versions());
		add(new ListView<ArtifactVersion>("artifactVersions", CollectionToListWrapperModel.of(setModel)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ArtifactVersion> item) {
				item.add(new Label("version", BindingModel.of(item.getModel(), Binding.artifactVersion().version())));
				item.add(new DateLabel("lastUpdateDate", BindingModel.of(item.getModel(), Binding.artifactVersion().lastUpdateDate()),
						DatePattern.SHORT_DATE));
				
				item.add(new ExternalLink("mavenCentralLink", mavenCentralSearchUrlService.getVersionUrl(item.getModelObject())));
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
}
