package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;

public class ArtifactPublicPanel extends GenericPanel<Artifact> {

	private static final long serialVersionUID = -8479708798509150198L;
	
	public ArtifactPublicPanel(String id, IModel<Artifact> artifactModel) {
		super(id, artifactModel);
		
		add(new Label("description", new ResourceModel("artifact.description.text")).setEscapeModelStrings(false));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(MavenArtifactNotifierSession.get().getUser() == null);
	}
}
