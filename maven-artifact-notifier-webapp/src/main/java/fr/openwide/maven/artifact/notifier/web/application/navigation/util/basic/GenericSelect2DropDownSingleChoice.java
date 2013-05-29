package fr.openwide.maven.artifact.notifier.web.application.navigation.util.basic;

import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.retzlaff.select2.Select2Behavior;
import org.retzlaff.select2.Select2Settings;

public abstract class GenericSelect2DropDownSingleChoice<E> extends DropDownChoice<E> {
	private static final long serialVersionUID = -3776700270762640109L;

	/**
	 * Taille du select en px. Par défaut, la taille définie par Bootstrap pour
	 * les select.
	 */
	private DropDownChoiceWidth width = DropDownChoiceWidth.NORMAL;

	protected GenericSelect2DropDownSingleChoice(String id, IModel<E> model,
			IModel<? extends List<? extends E>> choicesModel, IChoiceRenderer<? super E> renderer) {
		super(id);

		setModel(model);
		setChoices(choicesModel);
		setChoiceRenderer(renderer);
		setNullValid(true);

		Select2Behavior<E, E> select2Behavior = Select2Behavior.forChoice(this);
		fillSelect2Settings(select2Behavior.getSettings());
		add(select2Behavior);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		// Hack très moche qui permet de contourner un problème de Firefox qui
		// calcule mal les largeurs dans certains cas
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
		settings.setAllowClear(true);
	}

	protected String getRootKey() {
		return GenericSelect2DropDownSingleChoice.class.getSimpleName() + "." + this.getId();
	}

	@Override
	protected String getNullKey() {
		return getRootKey() + ".null";
	}

	@Override
	protected String getNullValidKey() {
		return getRootKey() + ".nullValid";
	}

	public void setWidth(DropDownChoiceWidth width) {
		this.width = width;
	}
}
