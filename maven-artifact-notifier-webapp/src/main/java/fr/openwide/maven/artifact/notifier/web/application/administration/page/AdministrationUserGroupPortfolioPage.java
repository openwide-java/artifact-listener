package fr.openwide.maven.artifact.notifier.web.application.administration.page;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.UserGroupPortfolioPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.form.UserGroupFormPopupPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.template.AdministrationTemplate;

public class AdministrationUserGroupPortfolioPage extends AdministrationTemplate {

	private static final long serialVersionUID = 2733071974944289365L;

	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;

	@SpringBean
	private IUserGroupService userGroupService;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(AdministrationUserGroupPortfolioPage.class)
				.build();
	}

	public AdministrationUserGroupPortfolioPage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("navigation.administration.usergroup"),
				AdministrationUserGroupPortfolioPage.linkDescriptor()));
		
		IModel<List<UserGroup>> userGroupListModel = new LoadableDetachableModel<List<UserGroup>>() {
			private static final long serialVersionUID = -4518288683578265677L;
			
			@Override
			protected List<UserGroup> load() {
				return userGroupService.list();
			}
		};
		
		add(new UserGroupPortfolioPanel("portfolio", userGroupListModel, configurer.getPortfolioItemsPerPage()));
		
		// User group create popup
		UserGroupFormPopupPanel userGroupCreatePanel = new UserGroupFormPopupPanel("userGroupCreatePopupPanel");
		add(userGroupCreatePanel);
		
		Button createUserGroup = new Button("createUserGroup");
		createUserGroup.add(new AjaxModalOpenBehavior(userGroupCreatePanel, MouseEvent.CLICK) {
			private static final long serialVersionUID = 5414159291353181776L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		add(createUserGroup);
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return AdministrationUserGroupPortfolioPage.class;
	}
}
