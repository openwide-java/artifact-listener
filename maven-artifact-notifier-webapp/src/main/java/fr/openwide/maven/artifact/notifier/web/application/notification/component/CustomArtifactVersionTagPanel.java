package fr.openwide.maven.artifact.notifier.web.application.notification.component;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import fr.openwide.maven.artifact.notifier.web.application.artifact.component.AbstractArtifactVersionTagPanel;

public class CustomArtifactVersionTagPanel extends AbstractArtifactVersionTagPanel {

	private static final long serialVersionUID = 5430854844428861413L;

	private static final String STYLE_FINAL_VERSION_TAG = "padding: 2px 4px; background: #3498DB; color: #FFFFFF; display: inline-block; font-size: 12px; line-height: 14px; border-radius: 3px; vertical-align: baseline;";
	
	private static final String STYLE_NON_FINAL_VERSION_TAG = "padding: 2px 4px; background: #F89456; color: #FFFFFF; display: inline-block; font-size: 12px; line-height: 14px; border-radius: 3px; vertical-align: baseline;";
	
	public CustomArtifactVersionTagPanel(String id, IModel<? extends String> model) {
		super(id, model);
		IModel<String> cssStyleModel = new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				String version = getModelObject();
				if (isFinalVersion(version)) {
					return STYLE_FINAL_VERSION_TAG;
				}
				return STYLE_NON_FINAL_VERSION_TAG;
			}
		};
		
		add(new CustomLabel("latestVersion", model, cssStyleModel.getObject()));
	}
}
