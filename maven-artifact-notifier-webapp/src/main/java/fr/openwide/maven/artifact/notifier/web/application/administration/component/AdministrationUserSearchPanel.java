package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AdministrationUserSearchPanel extends Panel {
	
	private static final long serialVersionUID = -6224313886789870489L;
	
	private IPageable pageable;
	
	private IModel<String> searchTermModel;
	
	public AdministrationUserSearchPanel(String id, IPageable pageable, IModel<String> searchTermModel) {
		super(id);
		
		this.pageable = pageable;
		
		this.searchTermModel = searchTermModel;
		
		Form<Void> form = new Form<Void>("form") {
			private static final long serialVersionUID = -584576228542906811L;
			@Override
			protected void onSubmit() {
				// Lors de la soumission d'un formulaire de recherche, on retourne sur la première page
				AdministrationUserSearchPanel.this.pageable.setCurrentPage(0);
				super.onSubmit();
			}
		};
		
		TextField<String> searchInput = new TextField<String>("searchInput", this.searchTermModel);
		form.add(searchInput);
		
		form.add(new SubmitLink("submit"));
		
		add(form);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (searchTermModel != null) {
			this.searchTermModel.detach();
		}
	}
}
