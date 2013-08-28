package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.html.list.GenericPortfolioPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.ReadOnlyModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserGroupDescriptionPage;

public class UserGroupPortfolioPanel extends GenericPortfolioPanel<UserGroup> {

	private static final long serialVersionUID = -2237967697027843105L;

	@SpringBean
	private IUserGroupService userGroupService;

	public UserGroupPortfolioPanel(String id, IModel<List<UserGroup>> userGroupListModel, int itemsPerPage) {
		super(id, userGroupListModel, itemsPerPage);
	}

	@Override
	protected void addItemColumns(Item<UserGroup> item, IModel<? extends UserGroup> userGroupModel) {
		Link<Void> nameLink = AdministrationUserGroupDescriptionPage.linkDescriptor(ReadOnlyModel.of(userGroupModel))
				.link("nameLink");
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
		return AdministrationUserGroupDescriptionPage.linkDescriptor(ReadOnlyModel.of(userGroupModel)).link(id);
	}

	@Override
	protected IModel<String> getActionText(IModel<? extends UserGroup> userGroupModel) {
		return new ResourceModel("common.portfolio.action.viewDetails");
	}

	@Override
	protected boolean hasWritePermissionOn(IModel<? extends UserGroup> userGroupModel) {
		UserGroup userGroup = (UserGroup) userGroupModel.getObject();
		return !userGroup.isLocked();
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
