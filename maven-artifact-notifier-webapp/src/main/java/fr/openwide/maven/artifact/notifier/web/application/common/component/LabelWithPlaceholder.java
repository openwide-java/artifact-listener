package fr.openwide.maven.artifact.notifier.web.application.common.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.springframework.util.StringUtils;

public class LabelWithPlaceholder extends Label {
	
	private static final long serialVersionUID = -7506344388420093898L;
	
	private static final ResourceModel DEFAULT_EMPTY_FIELD_MODEL = new ResourceModel("common.emptyField");
	
	private IModel<?> mainModel;
	
	private IModel<String> placeholderModel;
	
	private IModel<String> rawDataModel;
	
	private boolean hideIfEmpty = false;
	
	public LabelWithPlaceholder(String id, IModel<?> model) {
		this(id, model, DEFAULT_EMPTY_FIELD_MODEL);
	}
	
	public LabelWithPlaceholder(String id, IModel<?> model, IModel<String> placeholderModel) {
		super(id, model);
		
		this.mainModel = model;
		this.placeholderModel = placeholderModel;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		boolean mainModelIsEmpty = !StringUtils.hasText(getDefaultModelObjectAsString(getMainModelObject()));
		boolean rawDataModelIsEmpty = !StringUtils.hasText(getDefaultModelObjectAsString(getRawDataModelObject()));
		
		if (!mainModelIsEmpty) {
			setDefaultModel(mainModel);
		} else if (!rawDataModelIsEmpty) {
			setDefaultModel(rawDataModel);
		} else {
			setDefaultModel(placeholderModel);
		}
		
		setVisible(!(hideIfEmpty && mainModelIsEmpty && rawDataModelIsEmpty));
	}
	
	public boolean isHideIfEmpty() {
		return hideIfEmpty;
	}
	
	public void setHideIfEmpty(boolean hideIfEmpty) {
		this.hideIfEmpty = hideIfEmpty;
	}
	
	protected Object getMainModelObject() {
		if (mainModel != null) {
			return mainModel.getObject();
		}
		return null;
	}
	
	protected Object getRawDataModelObject() {
		if (rawDataModel != null) {
			return rawDataModel.getObject();
		}
		return null;
	}
	
	public void setRawDataModel(IModel<String> rawDataModel) {
		this.rawDataModel = rawDataModel;
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (mainModel != null) {
			mainModel.detach();
		}
		if (placeholderModel != null) {
			placeholderModel.detach();
		}
		if (rawDataModel != null) {
			rawDataModel.detach();
		}
	}
}