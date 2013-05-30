package fr.openwide.maven.artifact.notifier.web.application.artifact.model;

import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.more.markup.repeater.data.LoadableDetachableDataProvider;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.search.service.IMavenCentralSearchApiService;

public class ArtifactSearchDataProvider extends LoadableDetachableDataProvider<ArtifactBean> {

	private static final long serialVersionUID = -6735682878632622767L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactSearchDataProvider.class);

	@SpringBean
	private IMavenCentralSearchApiService mavenCentralSearchService;
	
	private IModel<String> globalSearchModel;
	
	private IModel<String> searchGroupModel;
	
	private IModel<String> searchArtifactModel;
	
	public ArtifactSearchDataProvider(IModel<String> globalSearchModel, IModel<String> searchGroupModel, IModel<String> searchArtifactModel) {
		Injector.get().inject(this);
		
		if (globalSearchModel == null || searchGroupModel == null || searchArtifactModel == null) {
			throw new IllegalArgumentException("Null models are not supported.");
		}
		
		this.globalSearchModel = globalSearchModel;
		this.searchGroupModel = searchGroupModel;
		this.searchArtifactModel = searchArtifactModel;
	}
	
	@Override
	public IModel<ArtifactBean> model(ArtifactBean artifact) {
		return new Model<ArtifactBean>(artifact);
	}
	
	@Override
	protected List<ArtifactBean> loadList(long first, long count) {
		// TODO : Appliquer des regex pour verifier l'entree utilisateur
		try {
			return mavenCentralSearchService.getArtifacts(globalSearchModel.getObject(), searchGroupModel.getObject(),
					searchArtifactModel.getObject(), (int) first, (int) count);
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve the artifacts for search: '" + globalSearchModel.getObject() + "'", e);
			return Lists.newArrayList();
		}
	}

	@Override
	protected long loadSize() {
		try {
			return mavenCentralSearchService.countArtifacts(globalSearchModel.getObject(), searchGroupModel.getObject(),
					searchArtifactModel.getObject());
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve the artifacts for search: '" + globalSearchModel.getObject() + "'", e);
			return 0;
		}
	}
	
	@Override
	public void detach() {
		super.detach();
		if (globalSearchModel != null) {
			globalSearchModel.detach();
		}
		if (searchGroupModel != null) {
			searchGroupModel.detach();
		}
		if (searchArtifactModel != null) {
			searchArtifactModel.detach();
		}
	}
}
