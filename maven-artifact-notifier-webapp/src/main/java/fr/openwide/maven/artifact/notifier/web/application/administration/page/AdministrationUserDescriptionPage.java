package fr.openwide.maven.artifact.notifier.web.application.administration.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.link.descriptor.parameter.CommonParameters;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.UserArtifactsPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.UserMembershipsPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.component.UserProfilPanel;
import fr.openwide.maven.artifact.notifier.web.application.administration.template.AdministrationTemplate;

public class AdministrationUserDescriptionPage extends AdministrationTemplate {

	private static final long serialVersionUID = -550100874222819991L;
	
	@SpringBean
	private IUserService userService;

	private IModel<User> userModel;
	
	public static IPageLinkDescriptor linkDescriptor(IModel<User> userModel) {
		return new LinkDescriptorBuilder()
				.page(AdministrationUserDescriptionPage.class)
				.map(CommonParameters.ID, userModel, User.class).mandatory()
				.build();
	}

	public AdministrationUserDescriptionPage(PageParameters parameters) {
		super(parameters);
		
		userModel = new GenericEntityModel<Long, User>(null);
		
		linkDescriptor(userModel).extractSafely(parameters, AdministrationUserPortfolioPage.linkDescriptor());
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("navigation.administration.user"),
				AdministrationUserPortfolioPage.linkDescriptor()));
		
		addBreadCrumbElement(new BreadCrumbElement(BindingModel.of(userModel, Binding.user().displayName()),
				AdministrationUserDescriptionPage.linkDescriptor(userModel)));
		
		add(new Label("pageTitle", BindingModel.of(userModel, Binding.user().displayName())));
		
		add(new UserProfilPanel("profile", userModel));
		add(new UserMembershipsPanel("groups", userModel));
		add(new UserArtifactsPanel("artifacts", userModel));
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return AdministrationUserPortfolioPage.class;
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		
		if (userModel != null) {
			userModel.detach();
		}
	}
}
