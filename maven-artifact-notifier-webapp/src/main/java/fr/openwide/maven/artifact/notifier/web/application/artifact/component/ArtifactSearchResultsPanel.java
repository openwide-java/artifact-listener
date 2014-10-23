package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.markup.html.navigation.paging.HideablePagingNavigator;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

public class ArtifactSearchResultsPanel extends Panel {

	private static final long serialVersionUID = 2168203516395191437L;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	private DataView<ArtifactBean> dataView;
	
	public ArtifactSearchResultsPanel(String id, final IDataProvider<ArtifactBean> dataProvider) {
		super(id);
		
		add(new Label("title", new ResourceModel("artifact.follow.search.results.title")));
		
		dataView = new ArtifactBeanDataView("artifacts", dataProvider, configurer.getArtifactSearchItemsPerPage());
		add(dataView, new HideablePagingNavigator("pager", dataView));
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			public boolean isVisible() {
				return dataProvider.size() == 0;
			}
		});
	}

	public DataView<ArtifactBean> getDataView() {
		return dataView;
	}
}
