package fr.openwide.maven.artifact.notifier.web.application.project.form;

import java.util.Collections;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.form.FormPanelMode;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.AbstractAjaxModalPopupPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.DelegatedMarkupPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectVersionService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

public class ProjectVersionFormPopupPanel extends AbstractAjaxModalPopupPanel<ProjectVersion> {

	private static final long serialVersionUID = 4914283916847151778L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectVersionFormPopupPanel.class);

	@SpringBean
	private IProjectService projectService;
	
	@SpringBean
	private IProjectVersionService projectVersionService;
	
	private FormPanelMode mode;
	
	private Form<ProjectVersion> form;

	public ProjectVersionFormPopupPanel(String id, FormPanelMode mode) {
		this(id, new GenericEntityModel<Long, ProjectVersion>(null), mode);
	}
	
	public ProjectVersionFormPopupPanel(String id, IModel<ProjectVersion> projectVersionModel) {
		this(id, projectVersionModel, FormPanelMode.EDIT);
	}
	
	public ProjectVersionFormPopupPanel(String id, IModel<ProjectVersion> projectVersionModel, FormPanelMode mode) {
		super(id, projectVersionModel);
		this.mode = mode;
	}
	
	@Override
	protected Component createHeader(String wicketId) {
		if (isAddMode()) {
			return new Label(wicketId, new ResourceModel("project.version.add"));
		} else {
			return new Label(wicketId, new StringResourceModel("project.version.edit", getModel()));
		}
	}

	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, ProjectVersionFormPopupPanel.class);
		
		form = new Form<ProjectVersion>("form", getModel());
		body.add(form);
		
		TextField<String> versionField = new TextField<String>("version", BindingModel.of(getModel(), Binding.projectVersion().version())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(ProjectVersionFormPopupPanel.this.isAddMode());
			}
		};
		versionField.setLabel(new ResourceModel("project.version.field.version"));
		versionField.setRequired(isAddMode());
		versionField.add(new ProjectVersionPatternValidator());
		form.add(versionField);
		
		form.add(new VersionAdditionalInformationFormComponentPanel("additionalInformationPanel",
				BindingModel.of(getModel(), Binding.projectVersion().additionalInformation())));
		
		return body;
	}

	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, ProjectVersionFormPopupPanel.class);
		
		// Validate button
		AjaxButton validate = new AjaxButton("save", form) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ProjectVersion projectVersion = ProjectVersionFormPopupPanel.this.getModelObject();
				
				if (StringUtils.hasText(projectVersion.getVersion())) {
					try {
						if (isAddMode()) {
							ProjectVersion duplicate = projectVersionService.getByProjectAndVersion(getProject(), projectVersion.getVersion());
							if (duplicate == null) {
								projectService.createProjectVersion(getProject(), projectVersion);
								getSession().success(getString("project.version.add.success"));
								closePopup(target);
								target.add(getPage());
							} else {
								LOGGER.warn("A project version with the same version already exists");
								getSession().error(getString("project.version.add.notUnique"));
							}
						} else {
							projectVersionService.update(projectVersion);
							getSession().success(getString("project.version.edit.success"));
							closePopup(target);
							target.add(getPage());
						}
					} catch (Exception e) {
						if (isAddMode()) {
							LOGGER.error("Error occured while adding the project version", e);
							getSession().error(getString("project.version.add.error"));
						} else {
							LOGGER.error("Error occured while updating the project version", e);
							getSession().error(getString("project.version.edit.error"));
						}
					}
				}
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		};
		validate.add(new Label("validateLabel", new ResourceModel("common.action.save")));
		footer.add(validate);
		
		// Cancel button
		AbstractLink cancel = new AbstractLink("cancel") {
			private static final long serialVersionUID = 1L;
		};
		addCancelBehavior(cancel);
		footer.add(cancel);
		
		return footer;
	}
	
	protected boolean isEditMode() {
		return FormPanelMode.EDIT.equals(mode);
	}

	protected boolean isAddMode() {
		return FormPanelMode.ADD.equals(mode);
	}
	
	@Override
	protected void onShow(AjaxRequestTarget target) {
		super.onShow(target);
		if (isAddMode()) {
			ProjectVersion version = new ProjectVersion(null);
			getModel().setObject(version);
		}
	}
	
	protected Project getProject() {
		return null;
	}
	
	public class ProjectVersionPatternValidator extends PatternValidator {

		private static final long serialVersionUID = 315971574858314679L;
		
		private static final String PROJECT_VERSION_VALIDATION_PATTERN = "[\\w-_\\.]{1,20}";
		
		public ProjectVersionPatternValidator() {
			super(PROJECT_VERSION_VALIDATION_PATTERN);
		}
		
		@Override
		protected ValidationError decorate(ValidationError error, IValidatable<String> validatable) {
			error.setKeys(Collections.singletonList("project.version.field.version.malformed"));
			return error;
		}
		
	}
}
