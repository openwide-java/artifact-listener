package fr.openwide.maven.artifact.notifier.web.application.artifact.form;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.AbstractAjaxModalPopupPanel;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component.DelegatedMarkupPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactNotificationRuleService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactNotificationRuleTypeDropDownChoice;

public class ArtifactNotificationRuleFormPopupPanel extends AbstractAjaxModalPopupPanel<ArtifactNotificationRule> {

	private static final long serialVersionUID = 4914283916847151778L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactNotificationRuleFormPopupPanel.class);

	@SpringBean
	private IArtifactNotificationRuleService artifactNotificationRuleService;
	
	private Form<ArtifactNotificationRule> ruleForm;

	public ArtifactNotificationRuleFormPopupPanel(String id) {
		super(id, new GenericEntityModel<Long, ArtifactNotificationRule>(null));
	}
	
	public ArtifactNotificationRuleFormPopupPanel(String id, IModel<ArtifactNotificationRule> ruleModel) {
		super(id, ruleModel);
	}

	@Override
	protected Component createHeader(String wicketId) {
		return new Label(wicketId, new ResourceModel("artifact.rules.edit"));
	}

	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, ArtifactNotificationRuleFormPopupPanel.class);
		
		ruleForm = new Form<ArtifactNotificationRule>("form", getModel());
		body.add(ruleForm);
		
		TextField<String> regexField = new RequiredTextField<String>("regex", BindingModel.of(ruleForm.getModel(),
				Binding.artifactNotificationRule().regex()));
		regexField.setLabel(new ResourceModel("artifact.rules.field.regex"));
		ruleForm.add(regexField);
		
		ArtifactNotificationRuleTypeDropDownChoice typeField = new ArtifactNotificationRuleTypeDropDownChoice("type",
				BindingModel.of(ruleForm.getModel(), Binding.artifactNotificationRule().type()));
		typeField.setLabel(new ResourceModel("artifact.rules.field.type"));
		typeField.setRequired(true);
		ruleForm.add(typeField);
		
		return body;
	}

	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, ArtifactNotificationRuleFormPopupPanel.class);
		
		// Validate button
		AjaxButton validate = new AjaxButton("save", ruleForm) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ArtifactNotificationRule rule = ArtifactNotificationRuleFormPopupPanel.this.getModelObject();
				FollowedArtifact followedArtifact = rule.getFollowedArtifact();
				
				if (StringUtils.hasText(rule.getRegex()) && rule.getType() != null) {
					try {
						if (artifactNotificationRuleService.isRuleValid(rule.getRegex())) {
							
							ArtifactNotificationRule ruleDuplicate =
									artifactNotificationRuleService.getByFollowedArtifactAndRegex(followedArtifact, rule.getRegex());
							if (ruleDuplicate == null || rule.equals(ruleDuplicate)) {
								artifactNotificationRuleService.update(rule);
								getSession().success(getString("artifact.rules.edit.success"));
								closePopup(target);
								target.add(getPage());
							} else {
								LOGGER.warn("A rule with the same regex already exists");
								getSession().error(getString("artifact.rules.add.notUnique"));
							}
						} else {
							LOGGER.error("Invalid rule regex");
							getSession().error(getString("artifact.rules.add.invalidRegex"));
						}
					} catch (Exception e) {
						LOGGER.error("Error occured while updating the rule", e);
						getSession().error(getString("artifact.rules.edit.error"));
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
}
