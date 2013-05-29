package fr.openwide.maven.artifact.notifier.web.application.administration.model;

import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.markup.repeater.data.LoadableDetachableDataProvider;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;

public class ArtifactDataProvider extends LoadableDetachableDataProvider<Artifact> {
	
	private static final long serialVersionUID = -8540890431031886412L;

	@SpringBean
	private IArtifactService artifactService;
	
	private IModel<String> searchTermModel;
	
	public ArtifactDataProvider(IModel<String> searchTerm) {
		super();
		this.searchTermModel = searchTerm;
		
		Injector.get().inject(this);
	}
	
	@Override
	public IModel<Artifact> model(Artifact item) {
		return new GenericEntityModel<Long, Artifact>(item);
	}

	@Override
	protected List<Artifact> loadList(long first, long count) {
		return artifactService.search(searchTermModel.getObject(), (int) count, (int) first);
	}

	@Override
	protected long loadSize() {
		return artifactService.countSearch(searchTermModel.getObject());
	}
	
	@Override
	public void detach() {
		super.detach();
		if (searchTermModel != null) {
			searchTermModel.detach();
		}
	}
}
