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

import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserGroupDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.html.list.GenericPortfolioPanel;
import fr.openwide.core.wicket.more.model.BindingModel;

public class UserGroupPortfolioPanel extends GenericPortfolioPanel<UserGroup> {

	private static final long serialVersionUID = -2237967697027843105L;

	@SpringBean
	private IUserGroupService userGroupService;

	public UserGroupPortfolioPanel(String id, IModel<List<UserGroup>> userGroupListModel, int itemsPerPage) {
		super(id, userGroupListModel, itemsPerPage);
	}

	@Override
	protected void addItemColumns(Item<UserGroup> item, IModel<? extends UserGroup> userGroupModel) {
		Link<UserGroup> nameLink = new BookmarkablePageLink<UserGroup>("nameLink", AdministrationUserGroupDescriptionPage.class, 
				LinkUtils.getUserGroupPageParameters(userGroupModel.getObject()));
		nameLink.add(new Label("name", BindingModel.of(userGroupModel, Binding.userGroup().name())));
		item.add(nameLink);
		item.add(new Label("description", BindingModel.of(userGroupModel, Binding.userGroup().description())));
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
	protected MarkupContainer getActionLink(String id, final IModel<? extends UserGroup> userGroupModel) {
		return new BookmarkablePageLink<UserGroup>(id, AdministrationUserGroupDescriptionPage.class, 
				LinkUtils.getUserGroupPageParameters(userGroupModel.getObject()));
	}

	@Override
	protected IModel<String> getActionText(IModel<? extends UserGroup> userGroupModel) {
		return new ResourceModel("common.portfolio.action.viewDetails");
	}

	@Override
	protected boolean hasWritePermissionOn(IModel<?> userGroupModel) {
		UserGroup userGroup = (UserGroup) userGroupModel.getObject();
		return !userGroup.getLocked();
	}

	@Override
	protected void doDeleteItem(IModel<? extends UserGroup> userGroupModel) throws ServiceException,
	SecurityServiceException {
		userGroupService.delete(userGroupModel.getObject());
	}

	@Override
	protected IModel<String> getDeleteConfirmationTitleModel(IModel<? extends UserGroup> userGroupModel) {
		return new StringResourceModel("administration.usergroup.delete.confirmation.title", null,
				new Object[] { userGroupModel.getObject().getName() });
	}

	@Override
	protected IModel<String> getDeleteConfirmationTextModel(IModel<? extends UserGroup> userGroupModel) {
		return new StringResourceModel("administration.usergroup.delete.confirmation.text", null,
				new Object[] { userGroupModel.getObject().getName() });
	}
}
