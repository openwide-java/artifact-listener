package fr.openwide.maven.artifact.notifier.web.application.artifact.model;

import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.more.markup.repeater.data.LoadableDetachableDataProvider;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;

public abstract class AbstractArtifactPomSearchDataProvider extends LoadableDetachableDataProvider<ArtifactBean> {

	private static final long serialVersionUID = -6735682878632622767L;
	
	private IModel<PomBean> pomBeanModel;
	
	public AbstractArtifactPomSearchDataProvider(IModel<PomBean> pomBeanModel) {
		Injector.get().inject(this);
		
		if (pomBeanModel == null) {
			throw new IllegalArgumentException("Null model is not supported.");
		}
		
		this.pomBeanModel = pomBeanModel;
	}
	
	@Override
	public IModel<ArtifactBean> model(ArtifactBean artifact) {
		return new Model<ArtifactBean>(artifact);
	}
	
	protected abstract List<ArtifactBean> loadData();
	
	@Override
	protected List<ArtifactBean> loadList(long first, long count) {
		if (getPomBean() != null) {
			return loadData().subList((int) first, (int) (first + count));
		}
		return Lists.newArrayList();
	}

	@Override
	protected long loadSize() {
		if (getPomBean() != null) {
			return loadData().size();
		}
		return 0;
	}

	protected PomBean getPomBean() {
		return pomBeanModel.getObject();
	}
}
