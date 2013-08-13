package fr.openwide.maven.artifact.notifier.web.application.artifact.form;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.AbstractAjaxModalPopupPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.DelegatedMarkupPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.project.form.VersionAdditionalInformationFormComponentPanel;

public class ArtifactVersionFormPopupPanel extends AbstractAjaxModalPopupPanel<ArtifactVersion> {

	private static final long serialVersionUID = 4914283916847151778L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactVersionFormPopupPanel.class);

	@SpringBean
	private IArtifactVersionService artifactVersionService;
	
	private Form<ArtifactVersion> form;

	public ArtifactVersionFormPopupPanel(String id) {
		super(id, new GenericEntityModel<Long, ArtifactVersion>(null));
	}
	
	public ArtifactVersionFormPopupPanel(String id, IModel<ArtifactVersion> artifactVersionModel) {
		super(id, artifactVersionModel);
	}

	@Override
	protected Component createHeader(String wicketId) {
		return new Label(wicketId, new StringResourceModel("artifact.version.edit", getModel()));
	}

	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, ArtifactVersionFormPopupPanel.class);
		
		form = new Form<ArtifactVersion>("form", getModel());
		form.add(new VersionAdditionalInformationFormComponentPanel("additionalInformationPanel",
				BindingModel.of(getModel(), Binding.artifactVersion().additionalInformation())));
		body.add(form);
		
		return body;
	}

	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, ArtifactVersionFormPopupPanel.class);
		
		// Validate button
		AjaxButton validate = new AjaxButton("save", form) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ArtifactVersion artifactVersion = ArtifactVersionFormPopupPanel.this.getModelObject();
				
				try {
					artifactVersionService.update(artifactVersion);
					getSession().success(getString("artifact.version.edit.success"));
					closePopup(target);
					target.add(getPage());
				} catch (Exception e) {
					LOGGER.error("Error occured while updating the artifact version", e);
					getSession().error(getString("artifact.version.edit.error"));
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
}
