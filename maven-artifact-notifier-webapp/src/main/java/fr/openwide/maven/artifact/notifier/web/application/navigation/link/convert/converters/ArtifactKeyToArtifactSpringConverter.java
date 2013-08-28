package fr.openwide.maven.artifact.notifier.web.application.navigation.link.convert.converters;

import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;

/**
 * Converts an ArtifactKey to an artifact.
 */
public class ArtifactKeyToArtifactSpringConverter implements GenericConverter {
	
	private IArtifactService artifactService;
	
	public ArtifactKeyToArtifactSpringConverter(IArtifactService artifactService) {
		this.artifactService = artifactService;
	}
	
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(ArtifactKey.class, Artifact.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		if (!(source instanceof ArtifactKey)) {
			throw new IllegalStateException("Source must be an ArtifactKey");
		}
		
		return artifactService.getByArtifactKey((ArtifactKey) source);
	}

}
