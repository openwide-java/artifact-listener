package fr.openwide.maven.artifact.notifier.web.application.common.component;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.core.wicket.more.markup.html.form.AutocompleteAjaxComponent;

public class UserGroupAutocompleteAjaxComponent extends AutocompleteAjaxComponent<UserGroup> {

	private static final long serialVersionUID = -2583062091216398638L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupAutocompleteAjaxComponent.class);

	private static final UserGroupChoiceRenderer USER_GROUP_CHOICE_RENDERER = new UserGroupChoiceRenderer();

	@SpringBean
	private IUserGroupService userGroupService;

	public UserGroupAutocompleteAjaxComponent(String id, IModel<UserGroup> userModel) {
		super(id, userModel);
		setChoiceRenderer(USER_GROUP_CHOICE_RENDERER);
	}

	@Override
	public List<UserGroup> getValues(String term) {
		try {
			return userGroupService.searchAutocomplete(term);
		} catch (Exception e) {
			LOGGER.error("User autocomplete search error", e);
			return Lists.newArrayList();
		}
	}

	@Override
	public UserGroup getValueOnSearchFail(String input) {
		return null;
	}

	private static final class UserGroupChoiceRenderer implements IChoiceRenderer<UserGroup> {
		private static final long serialVersionUID = 1L;
		
		@Override
		public Object getDisplayValue(UserGroup userGroup) {
			return userGroup != null ? userGroup.getName() : "";
		}
		
		@Override
		public String getIdValue(UserGroup userGroup, int index) {
			return userGroup != null ? userGroup.getId().toString() : "-1";
		}
	}
}
