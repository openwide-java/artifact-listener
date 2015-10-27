package fr.openwide.maven.artifact.notifier.web.application.common.component;

import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.wicket.more.markup.html.form.AutocompleteAjaxComponent;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

public class UserAutocompleteAjaxComponent extends AutocompleteAjaxComponent<User> {

	private static final long serialVersionUID = -7717935272455937918L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserAutocompleteAjaxComponent.class);

	private static final UserChoiceRenderer USER_CHOICE_RENDERER = new UserChoiceRenderer();

	@SpringBean
	private IUserService userService;

	public UserAutocompleteAjaxComponent(String id, IModel<User> userModel) {
		super(id, userModel);
		setChoiceRenderer(USER_CHOICE_RENDERER);
	}

	@Override
	public List<User> getValues(String term) {
		try {
			return userService.searchAutocomplete(term);
		} catch (Exception e) {
			LOGGER.error("User autocomplete search error", e);
			return Lists.newArrayList();
		}
	}

	@Override
	public User getValueOnSearchFail(String input) {
		return null;
	}

	private static final class UserChoiceRenderer extends ChoiceRenderer<User> {
		private static final long serialVersionUID = 1L;
		
		@Override
		public Object getDisplayValue(User user) {
			return user != null ? user.getUserName() : "";
		}
		
		@Override
		public String getIdValue(User user, int index) {
			return user != null ? user.getId().toString() : "-1";
		}
	}
}
