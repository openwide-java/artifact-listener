package fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters;

import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;

/**
 * Converts an Artifact to a String.
 */
public class ArtifactToStringSpringConverter implements GenericConverter {
	
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Artifact.class, String.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		if (!(source instanceof Artifact)) {
			throw new IllegalStateException("Source must be an Artifact");
		}
		
		return ((Artifact) source).getArtifactKey().getKey();
	}

}
