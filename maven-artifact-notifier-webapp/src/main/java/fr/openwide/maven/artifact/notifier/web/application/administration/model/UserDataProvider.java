package fr.openwide.maven.artifact.notifier.web.application.administration.model;

import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.repeater.data.LoadableDetachableDataProvider;
import fr.openwide.core.wicket.more.model.GenericEntityModel;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

public class UserDataProvider extends LoadableDetachableDataProvider<User> {
	
	private static final long serialVersionUID = -8540890431031886412L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDataProvider.class);

	@SpringBean
	private IUserService userService;
	
	private IModel<String> searchTermModel;
	
	public UserDataProvider(IModel<String> searchTerm) {
		super();
		this.searchTermModel = searchTerm;
		
		Injector.get().inject(this);
	}
	
	@Override
	public IModel<User> model(User item) {
		return new GenericEntityModel<Long, User>(item);
	}

	@Override
	protected List<User> loadList(long first, long count) {
		try {
			return userService.search(searchTermModel.getObject(), (int) count, (int) first);
		} catch (ServiceException e) {
			LOGGER.error("Unable to search for users.");
		}
		return Lists.newArrayListWithExpectedSize(0);
	}

	@Override
	protected long loadSize() {
		try {
			return userService.countSearch(searchTermModel.getObject());
		} catch (ServiceException e) {
			LOGGER.error("Unable to search for users.");
		}
		return 0;
	}
	
	@Override
	public void detach() {
		super.detach();
		if (searchTermModel != null) {
			searchTermModel.detach();
		}
	}
}
