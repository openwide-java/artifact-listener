package fr.openwide.maven.artifact.notifier.web.application.navigation.link;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.link.descriptor.parameter.CommonParameters;
import fr.openwide.core.wicket.more.link.factory.AbstractLinkFactory;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.web.application.navigation.link.parameter.mapping.ArtifactLinkParameterMappingEntry;

public final class LinkFactory extends AbstractLinkFactory {
	
	private static final LinkFactory INSTANCE = new LinkFactory();
	
	private LinkFactory() { }
	
	public static LinkFactory get() {
		return INSTANCE;
	}
	
	public IPageLinkDescriptor getAssortedArtifactPageLinkDescriptor(Class<? extends Page> pageClass, IModel<Artifact> artifactModel) {
		return new LinkDescriptorBuilder().page(pageClass)
				.map(new ArtifactLinkParameterMappingEntry(artifactModel)).mandatory()
				.build();
	}
	
	public IPageLinkDescriptor getAssortedProjectPageLinkDescriptor(Class<? extends Page> pageClass, IModel<Project> projectModel) {
		return new LinkDescriptorBuilder().page(pageClass)
				.map(CommonParameters.NATURAL_ID, projectModel, Project.class).mandatory()
				.build();
	}

}