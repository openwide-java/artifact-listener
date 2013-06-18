package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.retzlaff.select2.Select2Settings;

import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactDeprecationStatusDropDownChoice;

public class AdministrationArtifactSearchPanel extends Panel {
	
	private static final long serialVersionUID = -6224313886789870489L;
	
	private IPageable pageable;
	
	private IModel<String> searchTermModel;
	
	private IModel<ArtifactDeprecationStatus> deprecationModel;
	
	public AdministrationArtifactSearchPanel(String id, IPageable pageable, IModel<String> searchTermModel, IModel<ArtifactDeprecationStatus> deprecationModel) {
		super(id);
		
		this.pageable = pageable;
		
		this.searchTermModel = searchTermModel;
		this.deprecationModel = deprecationModel;
		
		Form<Void> form = new Form<Void>("form") {
			private static final long serialVersionUID = -584576228542906811L;
			@Override
			protected void onSubmit() {
				// Lors de la soumission d'un formulaire de recherche, on retourne sur la première page
				AdministrationArtifactSearchPanel.this.pageable.setCurrentPage(0);
				super.onSubmit();
			}
		};
		
		TextField<String> searchInput = new TextField<String>("searchInput", this.searchTermModel);
		form.add(searchInput);
		
		ArtifactDeprecationStatusDropDownChoice deprecationField = new ArtifactDeprecationStatusDropDownChoice("deprecation", deprecationModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void fillSelect2Settings(Select2Settings settings) {
				super.fillSelect2Settings(settings);
				settings.setAllowClear(true);
			}
		};
		deprecationField.setNullValid(true);
		form.add(deprecationField);
		
		form.add(new SubmitLink("submit"));
		
		add(form);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (searchTermModel != null) {
			searchTermModel.detach();
		}
		if (deprecationModel != null) {
			deprecationModel.detach();
		}
	}
}
