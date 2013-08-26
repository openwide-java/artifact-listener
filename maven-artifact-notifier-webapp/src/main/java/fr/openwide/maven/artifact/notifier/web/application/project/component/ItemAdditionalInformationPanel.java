package fr.openwide.maven.artifact.notifier.web.application.project.component;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import fr.openwide.core.wicket.markup.html.basic.CountLabel;
import fr.openwide.core.wicket.markup.html.basic.HideableExternalLink;
import fr.openwide.core.wicket.markup.html.basic.HideableLabel;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ItemAdditionalInformation;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectLicense;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

public class ItemAdditionalInformationPanel extends GenericPanel<ItemAdditionalInformation> {

	private static final long serialVersionUID = -1722482141482578013L;

	public ItemAdditionalInformationPanel(String id, IModel<? extends ItemAdditionalInformation> model) {
		super(id, model);

		// Website link
		add(new HideableExternalLink("websiteLink", BindingModel.of(model, Binding.itemAdditionalInformation().websiteUrl().url())));
		
		// Issue tracker link
		add(new HideableExternalLink("issueTrackerLink", BindingModel.of(model, Binding.itemAdditionalInformation().issueTrackerUrl().url())));
		
		// Scm link
		add(new HideableExternalLink("scmLink", BindingModel.of(model, Binding.itemAdditionalInformation().scmUrl().url())));
		
		// Changelog link
		add(new HideableExternalLink("changelogLink", BindingModel.of(model, Binding.itemAdditionalInformation().changelogUrl().url())));
		
		// Licenses
		final IModel<List<ProjectLicense>> licensesModel = BindingModel.of(model, Binding.itemAdditionalInformation().licenses());
		add(new CountLabel("licensesHeader", "project.description.links.licenses", new LoadableDetachableModel<Number>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Number load() {
				List<ProjectLicense> licenses = licensesModel.getObject();
				if (licenses != null) {
					return licenses.size();
				}
				return 0;
			}
		}));
		add(new ListView<ProjectLicense>("licenses", licensesModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ProjectLicense> item) {
				item.add(new HideableLabel("licenseShortLabel", BindingModel.of(item.getModel(), Binding.projectLicense().shortLabel())));
				
				item.add(new Label("licenseLabel", BindingModel.of(item.getModel(), Binding.projectLicense().label())));

				item.add(new HideableExternalLink("licenseLink", BindingModel.of(item.getModel(), Binding.projectLicense().licenseUrl())));
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				List<ProjectLicense> licenses = licensesModel.getObject();
				setVisible(licenses != null && !licenses.isEmpty());
			}
		});
	}
}
