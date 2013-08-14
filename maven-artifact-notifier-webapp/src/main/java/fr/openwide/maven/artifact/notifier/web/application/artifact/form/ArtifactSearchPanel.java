package fr.openwide.maven.artifact.notifier.web.application.artifact.form;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class ArtifactSearchPanel extends Panel {

	private static final long serialVersionUID = 6273289257800090393L;
	
	private IModel<String> globalSearchModel;
	
	private IModel<String> searchGroupModel;
	
	private IModel<String> searchArtifactModel;

	public ArtifactSearchPanel(String id, final IPageable pageable, IModel<String> globalSearchModel,
			IModel<String> searchGroupModel, IModel<String> searchArtifactModel) {
		super(id);
		
		this.globalSearchModel = globalSearchModel;
		this.searchGroupModel = searchGroupModel;
		this.searchArtifactModel = searchArtifactModel;
		
		Form<Void> keywordSearchForm = new StatelessForm<Void>("keywordSearchForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				ArtifactSearchPanel.this.searchGroupModel.setObject("");
				ArtifactSearchPanel.this.searchArtifactModel.setObject("");
				
				setResponsePage(getPage().getClass(),
						LinkUtils.getSearchPageParameters(ArtifactSearchPanel.this.globalSearchModel));
			}
		};
		
		keywordSearchForm.add(new TextField<String>("globalSearchInput", this.globalSearchModel));
		keywordSearchForm.add(new SubmitLink("submit"));
		
		add(keywordSearchForm);
		
		// NOTE: If this search pattern is going to be reused, it will need to pass its groupId and
		// artifactId terms through the page parameters
		Form<Void> advancedSearchForm = new StatelessForm<Void>("advancedSearchForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				// Lors de la soumission d'un formulaire de recherche, on retourne sur la première page
				pageable.setCurrentPage(0);
				
				ArtifactSearchPanel.this.globalSearchModel.setObject("");
				
				super.onSubmit();
			}
		};
		
		advancedSearchForm.add(new TextField<String>("searchGroupInput", this.searchGroupModel));
		advancedSearchForm.add(new TextField<String>("searchArtifactInput", this.searchArtifactModel));
		advancedSearchForm.add(new SubmitLink("submit"));
		
		advancedSearchForm.setVisible(false);
		
		add(advancedSearchForm);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (globalSearchModel != null) {
			this.globalSearchModel.detach();
		}
		if (searchGroupModel != null) {
			this.searchGroupModel.detach();
		}
		if (searchArtifactModel != null) {
			this.searchArtifactModel.detach();
		}
	}
}
