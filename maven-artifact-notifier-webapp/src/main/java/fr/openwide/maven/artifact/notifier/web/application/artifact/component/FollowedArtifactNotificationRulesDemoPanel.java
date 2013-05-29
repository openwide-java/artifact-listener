package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactNotificationRule;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactNotificationRuleService;

public class FollowedArtifactNotificationRulesDemoPanel extends GenericPanel<List<ArtifactNotificationRule>> {

	private static final long serialVersionUID = -3367288547953520711L;
	
	private static final String LABEL_SUCCESS_CLASS = "label label-success";
	
	private static final String LABEL_INFO_CLASS = "label label-info";
	
	private static final String LABEL_ERROR_CLASS = "label label-important";
	
	@SpringBean
	private IArtifactNotificationRuleService artifactNotificationRuleService;
	
	public FollowedArtifactNotificationRulesDemoPanel(String id, IModel<List<ArtifactNotificationRule>> rulesModel) {
		super(id, rulesModel);
		
		add(new Label("title", new ResourceModel("artifact.rules.demo.title")));
		
		add(new MultiLineLabel("demoDescription", new ResourceModel("artifact.rules.demo.text")));
		
		final IModel<String> demoTextModel = Model.of();
		final IModel<Boolean> willNotifyModel = Model.of(false);
		IModel<String> demoResultModel = new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				if (!StringUtils.hasText(demoTextModel.getObject())) {
					return getString("artifact.rules.demo.label.empty");
				} else if (willNotifyModel.getObject()) {
					return getString("artifact.rules.demo.label.willNotify");
				}
				return getString("artifact.rules.demo.label.willIgnore");
			}
		};
		
		final Label demoResultLabel = new Label("demoResultLabel", demoResultModel);
		demoResultLabel.setOutputMarkupId(true);
		demoResultLabel.add(new AttributeModifier("class", LABEL_INFO_CLASS));
		add(demoResultLabel);
		
		TextField<String> demoInput = new TextField<String>("demoInput", demoTextModel);
		demoInput.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				String version = demoTextModel.getObject();
				boolean willNotify = false;
				String textClass;

				if (StringUtils.hasText(version)) {
					willNotify = artifactNotificationRuleService.checkRulesForVersion(version, getModelObject());
					textClass = (willNotify) ? LABEL_SUCCESS_CLASS : LABEL_ERROR_CLASS;
				} else {
					textClass = LABEL_INFO_CLASS;
				}
				demoResultLabel.add(new AttributeModifier("class", textClass));

				willNotifyModel.setObject(willNotify);
				target.add(demoResultLabel);
			}
		});
		add(demoInput);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject() != null && !getModelObject().isEmpty());
	}
}
