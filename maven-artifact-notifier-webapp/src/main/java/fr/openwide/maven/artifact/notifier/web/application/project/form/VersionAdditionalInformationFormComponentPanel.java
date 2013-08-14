package fr.openwide.maven.artifact.notifier.web.application.project.form;

import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.project.model.VersionAdditionalInformation;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

public class VersionAdditionalInformationFormComponentPanel extends GenericPanel<VersionAdditionalInformation> {

	private static final long serialVersionUID = 3850571671067492475L;

	public VersionAdditionalInformationFormComponentPanel(String id, IModel<? extends VersionAdditionalInformation> model) {
		super(id, model);
		
		add(
			new UrlTextField("changelogUrl", BindingModel.of(model, Binding.versionAdditionalInformation().changelogUrl()))
				.setLabel(new ResourceModel("artifact.version.edit.additionalInformation.changelogUrl")),
			new UrlTextField("releaseNotesUrl", BindingModel.of(model, Binding.versionAdditionalInformation().releaseNotesUrl()))
				.setLabel(new ResourceModel("artifact.version.edit.additionalInformation.releaseNotesUrl")),
			new UrlTextField("announceUrl", BindingModel.of(model, Binding.versionAdditionalInformation().announceUrl()))
				.setLabel(new ResourceModel("artifact.version.edit.additionalInformation.announceUrl"))
		);
	}
}
