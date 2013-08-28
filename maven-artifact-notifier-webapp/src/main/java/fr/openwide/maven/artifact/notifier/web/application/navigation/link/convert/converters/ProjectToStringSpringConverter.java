package fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters;

import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;

/**
 * Converts a Project to a String.
 */
public class ProjectToStringSpringConverter implements GenericConverter {
	
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Project.class, String.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		if (!(source instanceof Project)) {
			throw new IllegalStateException("Source must be an Project");
		}
		
		return ((Project) source).getUri();
	}

}
