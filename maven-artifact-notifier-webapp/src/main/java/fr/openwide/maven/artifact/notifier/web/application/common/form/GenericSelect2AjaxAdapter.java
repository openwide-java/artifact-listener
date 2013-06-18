package fr.openwide.maven.artifact.notifier.web.application.common.form;

import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.retzlaff.select2.ISelect2AjaxAdapter;

public abstract class GenericSelect2AjaxAdapter<T> implements ISelect2AjaxAdapter<T> {

	private static final long serialVersionUID = -4654743240914954744L;
	
	protected final IChoiceRenderer<? super T> choiceRenderer;

	public GenericSelect2AjaxAdapter(IChoiceRenderer<? super T> choiceRenderer) {
		Injector.get().inject(this);
		this.choiceRenderer = choiceRenderer;
	}
	
	@Override
	public abstract T getChoice(String id);
	
	@Override
	public abstract List<T> getChoices(int start, int count, String filter);

	@Override
	public String getChoiceId(T object) {
		return choiceRenderer.getIdValue(object, 0 /* inutilisé */);
	}

	@Override
	public Object getDisplayValue(T object) {
		return choiceRenderer.getDisplayValue(object);
	}

}