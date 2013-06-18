package fr.openwide.maven.artifact.notifier.web.application.common.form;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.string.Strings;
import org.retzlaff.select2.ISelect2AjaxAdapter;
import org.retzlaff.select2.Select2Behavior;
import org.retzlaff.select2.Select2Settings;
import org.retzlaff.select2.Select2SingleChoice;

import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.basic.DropDownChoiceWidth;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.basic.Select2Utils;

public class GenericSelect2AjaxDropDownSingleChoice<T> extends Select2SingleChoice<T> {

	private static final long serialVersionUID = 6355575209286187233L;

	/**
	 * Taille du select en px. Par défaut, la taille définie par Bootstrap pour les select.
	 */
	private DropDownChoiceWidth width = DropDownChoiceWidth.NORMAL;

	protected GenericSelect2AjaxDropDownSingleChoice(String id, IModel<T> model, final ISelect2AjaxAdapter<T> adapter) {
		super(id, model, adapter);
		
		fillSelect2Settings(getSettings());
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		// Hack très moche qui permet de contourner un problème de Firefox qui calcule mal les largeurs dans certains cas
		add(new AttributeAppender("style", "width: " + width.getWidth() + "px"));
	}

	/**
	 * Workaround.<br>
	 * La méthode cliente, {@link Select2Behavior#renderHead(org.apache.wicket.Component, org.apache.wicket.markup.head.IHeaderResponse)},
	 * ne prend pas en charge la restitution de l'input lors de l'échec de validation d'un formulaire.<br>
	 * Vu que surcharger {@link Select2Behavior#renderHead(org.apache.wicket.Component, org.apache.wicket.markup.head.IHeaderResponse)}
	 * serait très fastidieux (et dangereux), on s'en charge donc ici...
	 */
	@Override
	protected List<T> getModelObjects() {
		T object = getConvertedInput();
		if (object == null) {
			return super.getModelObjects();
		}
		return Collections.singletonList(object);
	}
	
	/**
	 * Workaround.<br>
	 * Dans le cas où le modèle sous-jascent est un {@link IObjectClassAwareModel} (un {@link BindingModel} par exemple),
	 * la méthode {@link AbstractTextComponent#resolveType()} va pouvoir déterminer le type du modèle.<br>
	 * Du coup, la méthode {@link FormComponent#convertInput()} va totalement ignorer {@link Select2SingleChoice#convertValue(String[]))},
	 * ce qui ne nous arrange pas puisque c'est par cette méthode qu'on fait usage de {@link ISelect2AjaxAdapter#getChoice(String)}...<br>
	 * On doit donc forcer l'utilisation de la méthode {@link #convertValue(String[])}.
	 */
	@Override
	protected void convertInput() {
		String[] value = getInputAsArray();
		String tmp = value != null && value.length > 0 ? value[0] : null;
		if (getConvertEmptyInputStringToNull() && Strings.isEmpty(tmp)) {
			setConvertedInput(null);
		} else {
			try {
				setConvertedInput(convertValue(getInputAsArray()));
			} catch (ConversionException e) {
				error(newValidationError(e));
			}
		}
	}
	
	protected void fillSelect2Settings(Select2Settings settings) {
		Select2Utils.setDefaultSettings(settings);
		settings.setMinimumInputLength(2);
		settings.setAllowClear(true);
	}
	
	public void setWidth(DropDownChoiceWidth width) {
		this.width = width;
	}
}
