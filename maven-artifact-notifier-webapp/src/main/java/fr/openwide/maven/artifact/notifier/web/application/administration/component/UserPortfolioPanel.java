package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.markup.html.link.EmailLink;
import fr.openwide.core.wicket.more.markup.html.image.BooleanGlyphicon;
import fr.openwide.core.wicket.more.markup.html.list.GenericPortfolioPanel;
import fr.openwide.core.wicket.more.model.BindingModel;

public class UserPortfolioPanel extends GenericPortfolioPanel<User> {

	private static final long serialVersionUID = 6030960404037116497L;

	@SpringBean
	private IUserService userService;

	public UserPortfolioPanel(String id, IModel<List<User>> userListModel, int itemsPerPage) {
		super(id, userListModel, itemsPerPage);
	}

	@Override
	protected void addItemColumns(Item<User> item, IModel<? extends User> userModel) {
		Link<User> userNameLink = new BookmarkablePageLink<User>("userNameLink", AdministrationUserDescriptionPage.class, 
				LinkUtils.getUserPageParameters(userModel.getObject()));
		userNameLink.add(new Label("userName", BindingModel.of(userModel, Binding.user().userName())));
		item.add(userNameLink);
		item.add(new Label("fullName", BindingModel.of(userModel, Binding.user().fullName())));
		item.add(new BooleanGlyphicon("active", BindingModel.of(userModel, Binding.user().active())));
		item.add(new EmailLink("email", BindingModel.of(userModel, Binding.user().email())));
	}

	@Override
	protected boolean isActionAvailable() {
		return true;
	}

	@Override
	protected boolean isDeleteAvailable() {
		return MavenArtifactNotifierSession.get().hasRoleAdmin();
	}

	@Override
	protected boolean isEditAvailable() {
		return false;
	}

	@Override
	protected MarkupContainer getActionLink(String id, IModel<? extends User> userModel) {
		return new BookmarkablePageLink<User>(id, AdministrationUserDescriptionPage.class, 
				LinkUtils.getUserPageParameters(userModel.getObject()));
	}

	@Override
	protected IModel<String> getActionText(IModel<? extends User> userModel) {
		return new ResourceModel("common.portfolio.action.viewDetails");
	}

	@Override
	protected boolean hasWritePermissionOn(IModel<?> userModel) {
		return MavenArtifactNotifierSession.get().hasRoleAdmin();
	}

	@Override
	protected void doDeleteItem(IModel<? extends User> userModel) throws ServiceException, SecurityServiceException {
		userService.delete(userModel.getObject());
	}

	@Override
	protected IModel<String> getDeleteConfirmationTitleModel(IModel<? extends User> userModel) {
		return new StringResourceModel("administration.user.delete.confirmation.title", null, 
				new Object[] { userModel.getObject().getDisplayName() });
	}

	@Override
	protected IModel<String> getDeleteConfirmationTextModel(IModel<? extends User> userModel) {
		return new StringResourceModel("administration.user.delete.confirmation.text", null, 
				new Object[] { userModel.getObject().getDisplayName() });
	}
}
