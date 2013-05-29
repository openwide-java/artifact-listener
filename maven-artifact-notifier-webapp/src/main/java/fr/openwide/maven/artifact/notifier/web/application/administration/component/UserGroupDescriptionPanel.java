package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;

import fr.openwide.maven.artifact.notifier.core.business.authority.MavenArtifactNotifierAuthorityUtils;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.form.UserGroupFormPopupPanel;
import fr.openwide.core.jpa.security.business.authority.model.Authority;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.image.BooleanGlyphicon;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;

public class UserGroupDescriptionPanel extends GenericPanel<UserGroup> {

	private static final long serialVersionUID = 4372823586880908316L;
	
	@SpringBean
	private MavenArtifactNotifierAuthorityUtils authorityUtils;

	public UserGroupDescriptionPanel(String id, final IModel<UserGroup> userGroupModel) {
		super(id, userGroupModel);
		
		add(new WebMarkupContainer("lockedWarningContainer") {
			private static final long serialVersionUID = -6522648858912041466L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(userGroupModel.getObject().getLocked());
			}
		});
		
		add(new MultiLineLabel("description", BindingModel.of(userGroupModel, Binding.userGroup().description())));
		
		add(new ListView<Authority>("authorities", Model.ofList(authorityUtils.getPublicAuthorities())) {
			private static final long serialVersionUID = -4307272691513553800L;
			
			@Override
			protected void populateItem(ListItem<Authority> item) {
				Authority authority = item.getModelObject();
				item.add(new Label("authorityName", new ResourceModel(
						"administration.usergroup.authority." + authority.getName())));
				item.add(new BooleanGlyphicon("authorityCheck", Model.of(
						userGroupModel.getObject().getAuthorities().contains(authority))));
			}
		});
		
		// User group update popup
		UserGroupFormPopupPanel userGroupUpdatePanel = new UserGroupFormPopupPanel("userGroupUpdatePopupPanel", getModel());
		add(userGroupUpdatePanel);
		
		Button updateUserGroup = new Button("updateUserGroup") {
			private static final long serialVersionUID = 993019796184673872L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!UserGroupDescriptionPanel.this.getModelObject().getLocked());
			}
		};
		updateUserGroup.add(new AjaxModalOpenBehavior(userGroupUpdatePanel, MouseEvent.CLICK) {
			private static final long serialVersionUID = 5414159291353181776L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		add(updateUserGroup);
	}
}
