package fr.openwide.maven.artifact.notifier.web.application.artifact.model;

import java.util.Date;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;

public class ArtifactLastVersionModel extends LoadableDetachableModel<ArtifactVersion> {

	private static final long serialVersionUID = 4766320771806264081L;

	private IModel<Artifact> artifactModel;
	
	public ArtifactLastVersionModel(IModel<Artifact> artifactModel) {
		super();
		this.artifactModel = artifactModel;
		
		Injector.get().inject(this);
	}
	
	@Override
	protected ArtifactVersion load() {
		if (isLastVersionAvailable()) {
			return artifactModel.getObject().getLatestVersion();
		}
		return null;
	}
	
	public boolean isLastVersionAvailable() {
		return artifactModel.getObject() != null && artifactModel.getObject().getStatus() != ArtifactStatus.NOT_INITIALIZED;
	}
	
	public String getLastVersion() {
		if (getObject() != null) {
			return getObject().getVersion();
		}
		return null;
	}
	
	public Date getLastVersionUpdateDate() {
		if (getObject() != null) {
			return getObject().getLastUpdateDate();
		}
		return null;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (artifactModel != null) {
			artifactModel.detach();
		}
	}
}
