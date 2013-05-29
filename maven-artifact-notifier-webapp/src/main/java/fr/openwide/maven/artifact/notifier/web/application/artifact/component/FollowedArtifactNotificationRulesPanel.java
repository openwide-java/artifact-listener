package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.form.LabelPlaceholderBehavior;
import fr.openwide.core.wicket.more.markup.html.image.BooleanGlyphicon;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRuleType;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactNotificationRuleService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactNotificationRuleFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.basic.DropDownChoiceWidth;

public class FollowedArtifactNotificationRulesPanel extends GenericPanel<FollowedArtifact> {

	private static final long serialVersionUID = 1955579250974258074L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FollowedArtifactNotificationRulesPanel.class);

	@SpringBean
	private IFollowedArtifactService followedArtifactService;
	
	@SpringBean
	private IArtifactNotificationRuleService artifactNotificationRuleService;

	private ArtifactNotificationRuleFormPopupPanel ruleEditPopupPanel;
	
	private IModel<List<ArtifactNotificationRule>> rulesModel;
	
	private ListView<ArtifactNotificationRule> rulesListView;

	public FollowedArtifactNotificationRulesPanel(String id, IModel<FollowedArtifact> followedArtifactModel) {
		super(id, followedArtifactModel);
		
		add(new Label("title", new ResourceModel("artifact.rules.title")));
		
		// Rule update popup
		ruleEditPopupPanel = new ArtifactNotificationRuleFormPopupPanel("ruleUpdatePopupPanel");
		add(ruleEditPopupPanel);
		
		// Rules list
		rulesModel = new LoadableDetachableModel<List<ArtifactNotificationRule>>() {
			private static final long serialVersionUID = -8484961470906264804L;

			@Override
			protected List<ArtifactNotificationRule> load() {
				List<ArtifactNotificationRule> rules = followedArtifactService.listArtifactNotificationRules(getModel().getObject());
				Collections.sort(rules);
				return rules;
			}
		};
		
		rulesListView = new ListView<ArtifactNotificationRule>("rules", rulesModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final ListItem<ArtifactNotificationRule> item) {
				item.add(new Label("regex", BindingModel.of(item.getModel(), Binding.artifactNotificationRule().regex())));

				boolean complyRule = item.getModelObject().getType().equals(ArtifactNotificationRuleType.COMPLY);
				MarkupContainer icon = new BooleanGlyphicon("notifyMatches", Model.of(complyRule));
				String tooltipKey = (complyRule ? "artifact.rules.notifyMatches" : "artifact.rules.ignoreMatches");
				icon.add(new AttributeModifier("title", new ResourceModel(tooltipKey)));
				item.add(icon);
				
				// Update rule link
				Button updateRuleLink = new Button("editRuleLink");
				updateRuleLink.add(new AjaxModalOpenBehavior(ruleEditPopupPanel, MouseEvent.CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onShow(AjaxRequestTarget target) {
						ruleEditPopupPanel.getModel().setObject(item.getModelObject());
					}
				});
				item.add(updateRuleLink);

				// Delete rule link
				IModel<String> confirmationTextModel = new StringResourceModel(
						"artifact.rules.delete.confirmation.text",
						null, new Object[] {
								item.getModelObject().getRegex(),
								FollowedArtifactNotificationRulesPanel.this.getModelObject().getArtifact().getGroup().getGroupId(),
								FollowedArtifactNotificationRulesPanel.this.getModelObject().getArtifact().getArtifactId()
						}
				);
				
				item.add(new AjaxConfirmLink<ArtifactNotificationRule>("deleteLink", item.getModel(),
						new ResourceModel("artifact.rules.delete.confirmation.title"),
						confirmationTextModel,
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"), null, false) {
					private static final long serialVersionUID = -5179621361619239269L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							FollowedArtifact followedArtifact = FollowedArtifactNotificationRulesPanel.this.getModelObject();
							ArtifactNotificationRule rule = getModelObject();
							
							followedArtifactService.removeArtifactNotificationRule(followedArtifact, rule);
							Session.get().success(getString("artifact.rules.delete.success"));
						} catch (Exception e) {
							LOGGER.error("An error occured while removing the rule", e);
							Session.get().error(getString("artifact.rules.delete.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
				});
			}
		};
		add(rulesListView);
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(rulesListView.size() <= 0);
			}
		});
		
		// Add rule form
		final Form<Void> addRuleForm = new Form<Void>("addRuleForm");
		IModel<String> regexModel = Model.of();
		IModel<ArtifactNotificationRuleType> typeModel = Model.of(ArtifactNotificationRuleType.COMPLY);
		
		// Regex text field
		final TextField<String> regexTextField = new RequiredTextField<String>("regexInput", regexModel);
		regexTextField.setLabel(new ResourceModel("artifact.rules.field.regex"));
		regexTextField.add(new LabelPlaceholderBehavior());
		addRuleForm.add(regexTextField);
		
		// Type dropdown
		final ArtifactNotificationRuleTypeDropDownChoice typeDropDown = new ArtifactNotificationRuleTypeDropDownChoice("type", typeModel);
		typeDropDown.setRequired(true);
		typeDropDown.setWidth(DropDownChoiceWidth.SMALL);
		addRuleForm.add(typeDropDown);
		
		addRuleForm.add(new AjaxSubmitLink("addRuleLink", addRuleForm) {
			private static final long serialVersionUID = 6935376642872117563L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FollowedArtifact followedArtifact = FollowedArtifactNotificationRulesPanel.this.getModelObject();
				String regex = regexTextField.getModelObject();
				ArtifactNotificationRuleType type = typeDropDown.getModelObject();
				
				if (StringUtils.hasText(regex) && type != null) {
					try {
						if (artifactNotificationRuleService.isRuleValid(regex)) {
							if (artifactNotificationRuleService.getByFollowedArtifactAndRegex(followedArtifact, regex) == null) {
								followedArtifactService.addArtifactNotificationRule(followedArtifact, regex, type);
								getSession().success(getString("artifact.rules.add.success"));
							} else {
								LOGGER.error("A rule with the same regex already exists");
								getSession().error(getString("artifact.rules.add.notUnique"));
							}
						} else {
							LOGGER.error("Invalid rule regex");
							getSession().error(getString("artifact.rules.add.invalidRegex"));
						}
					} catch (Exception e) {
						LOGGER.error("Unknown error occured while creating the rule", e);
						getSession().error(getString("artifact.rules.add.error"));
					}
				}
				regexTextField.setModelObject("");
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
		add(addRuleForm);
		
		// Demonstration
		add(new FollowedArtifactNotificationRulesDemoPanel("demoPanel", rulesModel));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject() != null);
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		if (rulesModel != null) {
			rulesModel.detach();
		}
	}
}
