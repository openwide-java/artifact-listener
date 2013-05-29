package fr.openwide.maven.artifact.notifier.web.application.common.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public abstract class EitherModel<T> extends AbstractReadOnlyModel<T> {

	private static final long serialVersionUID = 5069343397373330352L;

	private final IModel<? extends T> firstModel;

	private final IModel<? extends T> secondModel;

	public EitherModel(IModel<? extends T> firstModel, IModel<? extends T> secondModel) {
		super();
		this.firstModel = firstModel;
		this.secondModel = secondModel;
	}

	@Override
	public T getObject() {
		if (shouldGetFirstModel()) {
			return firstModel.getObject();
		} else {
			return secondModel.getObject();
		}
	}

	protected abstract boolean shouldGetFirstModel();

	@Override
	public void detach() {
		super.detach();
		firstModel.detach();
		secondModel.detach();
	}
}
