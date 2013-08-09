package fr.openwide.maven.artifact.notifier.web.application.project.model;

import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.markup.repeater.data.LoadableDetachableDataProvider;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;

public class ProjectDataProvider extends LoadableDetachableDataProvider<Project> {
	
	private static final long serialVersionUID = -990342753394501234L;

	@SpringBean
	private IProjectService projectService;
	
	private IModel<String> searchTermModel;
	
	public ProjectDataProvider(IModel<String> searchTerm) {
		super();
		this.searchTermModel = searchTerm;
		
		Injector.get().inject(this);
	}
	
	@Override
	public IModel<Project> model(Project item) {
		return new GenericEntityModel<Long, Project>(item);
	}

	@Override
	protected List<Project> loadList(long first, long count) {
		return projectService.searchByName(searchTermModel.getObject(), (int) count, (int) first);
	}

	@Override
	protected long loadSize() {
		return projectService.countSearchByName(searchTermModel.getObject());
	}
	
	@Override
	public void detach() {
		super.detach();
		if (searchTermModel != null) {
			searchTermModel.detach();
		}
	}
}
