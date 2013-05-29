package fr.openwide.maven.artifact.notifier.web.application.notification.behavior;

import java.io.Serializable;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;

public class StyleAttributeAppender extends AttributeAppender {
	
	private static final long serialVersionUID = -1911156302254181459L;
	
	private static final String STYLE_ATTRIBUTE = "style";
	
	public StyleAttributeAppender(IModel<?> replaceModel) {
		super(STYLE_ATTRIBUTE, replaceModel);
	}
	
	public StyleAttributeAppender(Serializable value) {
		super(STYLE_ATTRIBUTE, value);
	}
}
