package fr.openwide.maven.artifact.notifier.web.application.project.component;

import org.apache.wicket.model.IModel;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

public class ProjectLinksPanel extends GenericPanel<Project> {

	private static final long serialVersionUID = -8479708798509150198L;

	public ProjectLinksPanel(String id, IModel<Project> projectModel) {
		super(id, projectModel);
		
		add(new ItemAdditionalInformationPanel("additionalInformationPanel",
				BindingModel.of(projectModel, Binding.project().additionalInformation())));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(
				getModelObject().getAdditionalInformation().getWebsiteUrl() != null ||
				getModelObject().getAdditionalInformation().getIssueTrackerUrl() != null ||
				getModelObject().getAdditionalInformation().getScmUrl() != null ||
				getModelObject().getAdditionalInformation().getChangelogUrl() != null ||
				!getModelObject().getAdditionalInformation().getLicenses().isEmpty()
		);
	}
}
