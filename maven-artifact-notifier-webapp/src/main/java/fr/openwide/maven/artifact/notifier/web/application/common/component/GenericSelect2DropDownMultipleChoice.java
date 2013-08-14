package fr.openwide.maven.artifact.notifier.web.application.common.component;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.retzlaff.select2.Select2Behavior;
import org.retzlaff.select2.Select2Settings;

import fr.openwide.core.wicket.markup.html.form.ListMultipleChoice;
import fr.openwide.core.wicket.more.markup.html.select2.util.DropDownChoiceWidth;
import fr.openwide.core.wicket.more.markup.html.select2.util.IDropDownChoiceWidth;
import fr.openwide.core.wicket.more.markup.html.select2.util.Select2Utils;

public abstract class GenericSelect2DropDownMultipleChoice<T> extends ListMultipleChoice<T> {
	
	private static final long serialVersionUID = -6179538711780820058L;
	
	/**
	 * Hack.
	 * @see IDropDownChoiceWidth
	 */
	private IDropDownChoiceWidth width = DropDownChoiceWidth.NORMAL;
	
	protected GenericSelect2DropDownMultipleChoice(String id, IModel<? extends Collection<T>> collectionModel,
			IModel<? extends List<? extends T>> choicesModel, IChoiceRenderer<? super T> renderer) {
		super(id, collectionModel, choicesModel, renderer);
		
		Select2Behavior<T, T> select2Behavior = Select2Behavior.forChoice(this);
		fillSelect2Settings(select2Behavior.getSettings());
		add(select2Behavior);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new AttributeAppender("style", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				return "width: " + width.getWidth() + "px";
			}
		}));
	}
	
	protected void fillSelect2Settings(Select2Settings settings) {
		Select2Utils.setDefaultSettings(settings);
	}
	
	public GenericSelect2DropDownMultipleChoice<T> setWidth(IDropDownChoiceWidth width) {
		this.width = width;
		return this;
	}
}
