package fr.openwide.maven.artifact.notifier.web.application.project.form;

import java.util.Collections;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.maven.artifact.notifier.web.application.project.component.ProjectLicenseDropDownChoice;

public class ProjectFormPopupPanel extends AbstractAjaxModalPopupPanel<Project> {

	private static final long serialVersionUID = 4914283916847151778L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectFormPopupPanel.class);

	@SpringBean
	private IProjectService projectService;
	
	private FormPanelMode mode;
	
	private Form<Project> form;

	public ProjectFormPopupPanel(String id, FormPanelMode mode) {
		this(id, new GenericEntityModel<Long, Project>(null), mode);
	}
	
	public ProjectFormPopupPanel(String id, IModel<Project> projectModel) {
		this(id, projectModel, FormPanelMode.EDIT);
	}
	
	public ProjectFormPopupPanel(String id, IModel<Project> projectModel, FormPanelMode mode) {
		super(id, projectModel);
		this.mode = mode;
	}
	
	@Override
	protected Component createHeader(String wicketId) {
		if (isAddMode()) {
			return new Label(wicketId, new ResourceModel("project.add"));
		} else {
			return new Label(wicketId, new StringResourceModel("project.edit", getModel()));
		}
	}

	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, ProjectFormPopupPanel.class);
		
		form = new Form<Project>("form", getModel());
		form.add(
				new RequiredTextField<String>("name", BindingModel.of(getModel(), Binding.project().name()))
						.setLabel(new ResourceModel("project.field.name"))
						.add(new ProjectNamePatternValidator()),
				new UrlTextField("websiteUrl", BindingModel.of(getModel(), Binding.project().additionalInformation().websiteUrl()))
						.setLabel(new ResourceModel("project.field.websiteUrl")),
				new UrlTextField("issueTrackerUrl", BindingModel.of(getModel(), Binding.project().additionalInformation().issueTrackerUrl()))
						.setLabel(new ResourceModel("project.field.issueTrackerUrl")),
				new UrlTextField("scmUrl", BindingModel.of(getModel(), Binding.project().additionalInformation().scmUrl()))
						.setLabel(new ResourceModel("project.field.scmUrl")),
				new UrlTextField("changelogUrl", BindingModel.of(getModel(), Binding.project().additionalInformation().changelogUrl()))
						.setLabel(new ResourceModel("project.field.changelogUrl")),
				new ProjectLicenseDropDownChoice("licenses", BindingModel.of(getModel(), Binding.project().additionalInformation().licenses()))
						.setLabel(new ResourceModel("project.field.licenses"))
		);
		body.add(form);
		
		return body;
	}

	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, ProjectFormPopupPanel.class);
		
		// Validate button
		AjaxButton validate = new AjaxButton("save", form) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Project project = ProjectFormPopupPanel.this.getModelObject();
				
				if (StringUtils.hasText(project.getName())) {
					try {
						Project duplicate = projectService.getByUri(StringUtils.urlize(project.getName()));

						if (isAddMode()) {
							if (duplicate == null) {
								project.setUri(project.getName());
								projectService.create(project);
								getSession().success(getString("project.add.success"));
								closePopup(target);
								target.add(getPage());
							} else {
								LOGGER.warn("A project with the same name already exists");
								getSession().error(getString("project.add.notUnique"));
							}
						} else {
							if (duplicate == null || project.equals(duplicate)) {
								project.setUri(project.getName());
								projectService.update(project);
								getSession().success(getString("project.edit.success"));
								closePopup(target);
								throw new RestartResponseException(getPage().getClass(), LinkUtils.getProjectPageParameters(project));
							} else {
								LOGGER.warn("A project with the same name already exists");
								getSession().error(getString("project.add.notUnique"));
							}
						}
					} catch (RestartResponseException e) {
						throw e;
					} catch (Exception e) {
						if (isAddMode()) {
							LOGGER.error("Error occured while adding the project", e);
							getSession().error(getString("project.add.error"));
						} else {
							LOGGER.error("Error occured while updating the project", e);
							getSession().error(getString("project.edit.error"));
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
			getModel().setObject(new Project());
		}
	}
	
	@Override
	protected IModel<String> getCssClassNamesModel() {
		return Model.of("modal-project-form");
	}
	
	public class ProjectNamePatternValidator extends PatternValidator {

		private static final long serialVersionUID = 315971574858314679L;
		
		private static final String PROJECT_NAME_VALIDATION_PATTERN = ".{2,25}";
		
		public ProjectNamePatternValidator() {
			super(PROJECT_NAME_VALIDATION_PATTERN);
		}
		
		@Override
		protected ValidationError decorate(ValidationError error, IValidatable<String> validatable) {
			error.setKeys(Collections.singletonList("project.field.name.malformed"));
			return error;
		}
		
	}
}
