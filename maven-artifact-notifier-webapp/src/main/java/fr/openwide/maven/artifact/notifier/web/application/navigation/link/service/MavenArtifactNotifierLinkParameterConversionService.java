package fr.openwide.maven.artifact.notifier.web.application.navigation.link.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.core.wicket.more.link.service.DefaultLinkParameterConversionService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters.ArtifactToStringSpringConverter;
import fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters.ProjectToStringSpringConverter;
import fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters.StringToArtifactSpringConverter;
import fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters.StringToProjectSpringConverter;

public class MavenArtifactNotifierLinkParameterConversionService extends DefaultLinkParameterConversionService {
	
	@Autowired
	private IArtifactService artifactService;
	
	@Autowired
	private IProjectService projectService;
	
	@Override
	@PostConstruct
	protected void initConverters() {
		addConverter(new StringToArtifactSpringConverter(artifactService));
		addConverter(new ArtifactToStringSpringConverter());
		
		addConverter(new StringToProjectSpringConverter(projectService));
		addConverter(new ProjectToStringSpringConverter());
		
		super.initConverters();
	}

}
