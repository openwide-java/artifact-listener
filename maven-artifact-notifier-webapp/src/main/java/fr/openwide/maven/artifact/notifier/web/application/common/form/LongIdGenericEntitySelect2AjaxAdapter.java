package fr.openwide.maven.artifact.notifier.web.application.common.form;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.maven.artifact.notifier.web.application.common.converter.LongIdEntityConverter;

public abstract class LongIdGenericEntitySelect2AjaxAdapter<T extends GenericEntity<Long, ?>> extends GenericSelect2AjaxAdapter<T> {
	
	private static final long serialVersionUID = 7880845591046909801L;
	
	private final LongIdEntityConverter<T> entityConverter;

	public LongIdGenericEntitySelect2AjaxAdapter(Class<T> clazz, IChoiceRenderer<? super T> choiceRenderer) {
		super(choiceRenderer);
		this.entityConverter = new LongIdEntityConverter<T>(clazz);
	}

	@Override
	public final T getChoice(String id) {
		return entityConverter.convertToObject(id, null /* unused */);
	}

}
