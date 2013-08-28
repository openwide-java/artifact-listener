package fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters;

import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;

/**
 * Converts a String (artifact key) to an artifact.
 */
public class StringToArtifactSpringConverter implements GenericConverter {
	
	private IArtifactService artifactService;
	
	public StringToArtifactSpringConverter(IArtifactService artifactService) {
		this.artifactService = artifactService;
	}
	
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Artifact.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		if (!(source instanceof String)) {
			throw new IllegalStateException("Source must be an String");
		}
		
		return artifactService.getByArtifactKey(new ArtifactKey((String) source));
	}

}
