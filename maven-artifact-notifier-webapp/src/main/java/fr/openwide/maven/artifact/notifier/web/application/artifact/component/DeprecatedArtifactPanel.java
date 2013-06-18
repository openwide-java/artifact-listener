package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class DeprecatedArtifactPanel extends GenericPanel<Artifact> {

	private static final long serialVersionUID = -3067355181564265555L;

	public DeprecatedArtifactPanel(String id, IModel<? extends Artifact> model) {
		super(id, model);
		
		final IModel<Artifact> relatedArtifactModel = BindingModel.of(getModel(), Binding.artifact().relatedArtifact());
		
		WebMarkupContainer container = new WebMarkupContainer("deprecatedContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(ArtifactDeprecationStatus.DEPRECATED.equals(getModelObject().getDeprecationStatus()));
			}
		};
		
		container.add(new Label("deprecated", new ResourceModel("artifact.description.deprecated")));
		
		WebMarkupContainer relatedArtifactContainer = new WebMarkupContainer("relatedArtifactContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(relatedArtifactModel.getObject() != null);
			}
		};
		container.add(relatedArtifactContainer);
		
		Link<Artifact> relatedArtifactLink = new BookmarkablePageLink<Artifact>("relatedArtifactLink", ArtifactDescriptionPage.class,
				LinkUtils.getArtifactPageParameters(relatedArtifactModel.getObject()));
		relatedArtifactLink.add(new Label("relatedArtifact", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				Artifact relatedArtifact = relatedArtifactModel.getObject();
				if (relatedArtifact != null) {
					return new StringBuilder(relatedArtifact.getGroup().getGroupId())
						.append(":")
						.append(relatedArtifact.getArtifactId())
						.toString();
				}
				return null;
			}
		}));
		relatedArtifactContainer.add(relatedArtifactLink);
		add(container);
	}
}
