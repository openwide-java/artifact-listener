package fr.openwide.maven.artifact.notifier.web.application.common.form;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

public abstract class AbstractInputPrerequisiteBehavior<T> extends Behavior {

	private static final long serialVersionUID = 4689707482303046984L;

	private final FormComponent<T> prerequisiteField;

	public AbstractInputPrerequisiteBehavior(FormComponent<T> prerequisiteField) {
		super();
		this.prerequisiteField = prerequisiteField;
	}

	@Override
	public final void onConfigure(Component component) {
		super.onConfigure(component);

		prerequisiteField.inputChanged();
		prerequisiteField.validate();

		if (shouldSetUpAttachedComponent(prerequisiteField)) {
			setUpAttachedComponent(component);
		} else {
			cleanDefaultModelObject(component);
			takeDownAttachedComponent(component);
		}

		prerequisiteField.getFeedbackMessages().clear();
		prerequisiteField.clearInput();
	}

	protected boolean shouldSetUpAttachedComponent(FormComponent<T> prerequisiteField) {
		return !Strings.isEmpty(prerequisiteField.getValue()) && prerequisiteField.isValid();
	}

	protected void cleanDefaultModelObject(Component attachedComponent) {
		IModel<?> model = attachedComponent.getDefaultModel();
		if (model != null) {
			model.setObject(null);
		}
	}

	protected abstract void setUpAttachedComponent(Component attachedComponent);

	protected abstract void takeDownAttachedComponent(Component attachedComponent);

	public FormComponent<T> getPrerequisiteField() {
		return prerequisiteField;
	}
}
