package fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters;

import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;

/**
 * Converts a String (project uri) to a project.
 */
public class StringToProjectSpringConverter implements GenericConverter {
	
	private IProjectService projectService;
	
	public StringToProjectSpringConverter(IProjectService projectService) {
		this.projectService = projectService;
	}
	
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Project.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		if (!(source instanceof String)) {
			throw new IllegalStateException("Source must be an String");
		}
		
		return projectService.getByUri((String) source);
	}

}
