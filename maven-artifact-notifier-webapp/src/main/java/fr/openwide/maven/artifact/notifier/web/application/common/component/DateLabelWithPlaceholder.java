package fr.openwide.maven.artifact.notifier.web.application.common.component;

import java.util.Date;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.springframework.util.StringUtils;

import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.util.IDatePattern;

public class DateLabelWithPlaceholder extends DateLabel {
	private static final long serialVersionUID = 7214422620839758144L;
	
	private static final ResourceModel DEFAULT_EMPTY_FIELD_MODEL = new ResourceModel("common.emptyField");
	
	private IModel<Date> mainModel;
	
	private IModel<String> placeholderModel;
	
	public DateLabelWithPlaceholder(String id, IModel<Date> model, IDatePattern datePattern) {
		this(id, model, datePattern, DEFAULT_EMPTY_FIELD_MODEL);
	}
	
	public DateLabelWithPlaceholder(String id, IModel<Date> dateModel, IDatePattern datePattern, IModel<String> placeholderModel) {
		super(id, dateModel, datePattern);
		
		this.mainModel = dateModel;
		this.placeholderModel = placeholderModel;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		if (StringUtils.hasText(getDefaultModelObjectAsString(getMainModelObject()))) {
			setDefaultModel(mainModel);
		} else {
			setDefaultModel(placeholderModel);
		}
	}
	
	public Date getMainModelObject() {
		if (mainModel != null) {
			return mainModel.getObject();
		}
		return null;
	}

}
