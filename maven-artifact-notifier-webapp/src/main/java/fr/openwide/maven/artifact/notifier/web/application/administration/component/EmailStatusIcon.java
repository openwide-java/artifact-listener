package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailStatus;

/**
 * Container utilisant les icones font-awesome et affichant
 * un tick ou une croix selon que le model soit true ou false.
 */
public class EmailStatusIcon extends WebMarkupContainer {

	private static final long serialVersionUID = -7046943814231028574L;

	private static final String CLASS_ATTRIBUTE = "class";
	private static final String TOOLTIP_ATTRIBUTE = "data-original-title";
	private static final String SEPARATOR = " ";

	private static final String BOOTSTRAP_PENDING_CONFIRM_ICON_CLASS = "fa fa-fw fa-clock-o";
	private static final String BOOTSTRAP_VALIDATED_ICON_CLASS = "fa fa-fw fa-check";
	private static final String BOOTSTRAP_PENDING_DELETE_ICON_CLASS = "fa fa-fw fa-clock-o";
	
	public EmailStatusIcon(String id, IModel<EmailStatus> statusModel) {
		super(id, statusModel);
	}

	@Override
	public void onComponentTag(final ComponentTag tag) {
		EmailStatus value = getValue();
		
		if (value != null) {
			String iconClass = "";
			String tooltipKey = getString("profile.email.status." + value.toString());
			if (value == EmailStatus.PENDING_CONFIRM) {
				iconClass = BOOTSTRAP_PENDING_CONFIRM_ICON_CLASS;
			} else if (value == EmailStatus.PENDING_DELETE) {
				iconClass = BOOTSTRAP_PENDING_DELETE_ICON_CLASS;
			} else if (value == EmailStatus.VALIDATED) {
				iconClass = BOOTSTRAP_VALIDATED_ICON_CLASS;
			}
			tag.append(CLASS_ATTRIBUTE, iconClass, SEPARATOR);
			tag.append(TOOLTIP_ATTRIBUTE, tooltipKey, SEPARATOR);
		}
		super.onComponentTag(tag);
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && (getValue() != null);
	}

	private EmailStatus getValue() {
		return (EmailStatus) getDefaultModelObject();
	}
}
