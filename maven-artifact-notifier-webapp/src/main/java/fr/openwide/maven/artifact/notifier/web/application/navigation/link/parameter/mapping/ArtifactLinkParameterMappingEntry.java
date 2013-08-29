package fr.openwide.maven.artifact.notifier.web.application.navigation.link.parameter.mapping;

import org.apache.wicket.Component;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;
import org.springframework.core.convert.ConversionException;

import fr.openwide.core.wicket.more.link.descriptor.builder.impl.CoreLinkDescriptorBuilderMandatoryParameterValidator;
import fr.openwide.core.wicket.more.link.descriptor.parameter.extractor.LinkParameterExtractionException;
import fr.openwide.core.wicket.more.link.descriptor.parameter.injector.LinkParameterInjectionException;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.ILinkParameterMappingEntry;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.ILinkParameterValidator;
import fr.openwide.core.wicket.more.link.service.ILinkParameterConversionService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;

public class ArtifactLinkParameterMappingEntry implements ILinkParameterMappingEntry<Artifact> {
	
	private static final long serialVersionUID = -8371083298437034295L;

	public static final String GROUP_ID_PARAMETER = "groupId";
	
	public static final String ARTIFACT_ID_PARAMETER = "artifactId";
	
	private IModel<Artifact> artifactModel;
	
	public ArtifactLinkParameterMappingEntry(IModel<Artifact> artifactModel) {
		this.artifactModel = artifactModel;
	}
	
	@Override
	public void inject(PageParameters targetParameters, ILinkParameterConversionService conversionService)
			throws LinkParameterInjectionException {
		Args.notNull(targetParameters, "targetParameters");
		Args.notNull(conversionService, "conversionService");
		
		Artifact artifact = artifactModel.getObject();
		
		if (artifact != null) {
			if (artifact.getGroup() != null && artifact.getGroup().getGroupId() != null) {
				targetParameters.add(GROUP_ID_PARAMETER, artifact.getGroup().getGroupId());
			}
			if (artifact.getArtifactId() != null) {
				targetParameters.add(ARTIFACT_ID_PARAMETER, artifact.getArtifactId());
			}
		}
	}

	@Override
	public void extract(PageParameters sourceParameters, ILinkParameterConversionService conversionService)
			throws LinkParameterExtractionException {
		Args.notNull(sourceParameters, "sourceParameters");
		Args.notNull(conversionService, "conversionService");
		
		String groupId = sourceParameters.get(GROUP_ID_PARAMETER).toString();
		String artifactId = sourceParameters.get(ARTIFACT_ID_PARAMETER).toString();
		
		Artifact artifact = null;
		if (groupId != null && artifactId != null) {
			ArtifactKey artifactKey = new ArtifactKey(groupId, artifactId);
			
			try {
				artifact = conversionService.convert(artifactKey, Artifact.class);
			} catch (ConversionException e) {
				throw new LinkParameterExtractionException(e);
			}
		}
		artifactModel.setObject(artifact);
	}

	@Override
	public ILinkParameterMappingEntry<Artifact> wrap(Component component) {
		IModel<Artifact> newModel;
		if (artifactModel instanceof IComponentAssignedModel) {
			newModel = ((IComponentAssignedModel<Artifact>) artifactModel).wrapOnAssignment(component);
		} else {
			newModel = artifactModel;
		}
		return new ArtifactLinkParameterMappingEntry(newModel);
	}
	
	@Override
	public ILinkParameterValidator mandatoryValidator() {
		return new CoreLinkDescriptorBuilderMandatoryParameterValidator(GROUP_ID_PARAMETER, ARTIFACT_ID_PARAMETER);
	}
	
	@Override
	public void detach() {
		artifactModel.detach();
	}

}
