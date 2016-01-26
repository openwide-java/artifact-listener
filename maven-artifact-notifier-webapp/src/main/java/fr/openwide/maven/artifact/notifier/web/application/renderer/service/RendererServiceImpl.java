package fr.openwide.maven.artifact.notifier.web.application.renderer.service;

import fr.openwide.core.wicket.more.rendering.service.AbstractRendererServiceImpl;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierApplication;

public class RendererServiceImpl extends AbstractRendererServiceImpl {
	
	@Override
	protected String getApplicationName() {
		return MavenArtifactNotifierApplication.NAME;
	}

}
