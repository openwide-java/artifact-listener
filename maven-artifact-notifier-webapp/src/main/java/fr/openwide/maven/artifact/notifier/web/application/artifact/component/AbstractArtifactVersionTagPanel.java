package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.regex.Pattern;

import org.apache.wicket.model.IModel;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;

public abstract class AbstractArtifactVersionTagPanel extends GenericPanel<String> {

	private static final long serialVersionUID = -802075278729209091L;
	
	private static final Pattern NON_FINAL_VERSION_PATTERN = Pattern.compile(".*[\\.-](rc|cr|beta|alpha|incubating).*", Pattern.CASE_INSENSITIVE);

	public AbstractArtifactVersionTagPanel(String id, IModel<? extends String> model) {
		super(id, model);
	}
	
	protected boolean isFinalVersion(String version) {
		return version == null || !NON_FINAL_VERSION_PATTERN.matcher(version).matches();
	}
}
