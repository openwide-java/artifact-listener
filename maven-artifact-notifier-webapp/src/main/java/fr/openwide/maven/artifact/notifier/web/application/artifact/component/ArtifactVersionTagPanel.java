package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.maven.artifact.notifier.web.application.common.component.LabelWithPlaceholder;

public class ArtifactVersionTagPanel extends AbstractArtifactVersionTagPanel {

	private static final long serialVersionUID = -8022735891213157213L;

	private static final String CSS_FINAL_VERSION_TAG = "label label-info";
	
	private static final String CSS_NON_FINAL_VERSION_TAG = "label label-warning";
	
	public ArtifactVersionTagPanel(String id, IModel<? extends String> model) {
		this(id, model, true);
	}
	
	public ArtifactVersionTagPanel(String id, IModel<? extends String> model, boolean showPlaceholder) {
		super(id, model);
		IModel<String> cssClassModel = new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				String version = getModelObject();
				if (isFinalVersion(version)) {
					return CSS_FINAL_VERSION_TAG;
				}
				return CSS_NON_FINAL_VERSION_TAG;
			}
		};
		
		LabelWithPlaceholder latestVersionLabel = new LabelWithPlaceholder("latestVersion", model);
		latestVersionLabel.setHideIfEmpty(!showPlaceholder);
		latestVersionLabel.add(new ClassAttributeAppender(cssClassModel));
		add(latestVersionLabel);
	}
}
