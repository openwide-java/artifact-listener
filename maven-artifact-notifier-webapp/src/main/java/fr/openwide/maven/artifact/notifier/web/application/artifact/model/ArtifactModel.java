package fr.openwide.maven.artifact.notifier.web.application.artifact.model;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;

public class ArtifactModel extends LoadableDetachableModel<Artifact> {

	private static final long serialVersionUID = -4753990112314070463L;
	
	@SpringBean
	private IArtifactService artifactService;
	
	private IModel<ArtifactKey> artifactKeyModel;
	
	public ArtifactModel(IModel<ArtifactKey> artifactKeyModel) {
		super();
		this.artifactKeyModel = artifactKeyModel;
		
		Injector.get().inject(this);
	}
	
	@Override
	protected Artifact load() {
		return artifactService.getByArtifactKey(artifactKeyModel.getObject());
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (artifactKeyModel != null) {
			artifactKeyModel.detach();
		}
	}
}
